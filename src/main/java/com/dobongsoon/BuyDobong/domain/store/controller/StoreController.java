package com.dobongsoon.BuyDobong.domain.store.controller;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.common.s3.S3Service;
import com.dobongsoon.BuyDobong.domain.consumer.recent.service.RecentStoreService;
import com.dobongsoon.BuyDobong.domain.consumer.model.Consumer;
import com.dobongsoon.BuyDobong.domain.consumer.repository.ConsumerRepository;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreCreateRequest;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreDetailDto;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreOpenRequest;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreResponse;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreUpdateRequest;
import com.dobongsoon.BuyDobong.domain.store.service.StoreQueryService;
import com.dobongsoon.BuyDobong.domain.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final StoreQueryService storeQueryService;
    private final RecentStoreService recentStoreService;
    private final ConsumerRepository consumerRepository;
    private final S3Service s3Service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MERCHANT')")
    @Operation(summary = "상점 등록 (이미지 + JSON 통합 버전)")
    public ResponseEntity<StoreResponse> createStoreMultipart(
            @AuthenticationPrincipal Long userId,
            @RequestPart("data") @Valid StoreCreateRequest req,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        if (image != null && !image.isEmpty()) {
            String url = s3Service.uploadStoreImage(userId, image);
            req.setImageUrl(url);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(storeService.register(userId, req));
    }

    @PatchMapping(value = "/{storeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MERCHANT')")
    @Operation(summary = "상점 수정 (이미지 + JSON 통합 버전)")
    public ResponseEntity<StoreResponse> updateStore(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long storeId,
            @RequestPart("data") @Valid StoreUpdateRequest req,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        if (image != null && !image.isEmpty()) {
            String url = s3Service.uploadStoreImage(userId, image);
            req.setImageUrl(url); // 업로드된 URL을 DTO에 주입
        }

        StoreResponse response = storeService.update(userId, req);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('MERCHANT')")
    @Operation(summary = "내 상점 조회")
    public ResponseEntity<StoreResponse> getMyStore(@AuthenticationPrincipal Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return ResponseEntity.ok(storeService.getMyStore(userId));
    }

    @PostMapping("/open")
    @PreAuthorize("hasRole('MERCHANT')")
    @Operation(summary = "영업 ON/OFF")
    public ResponseEntity<StoreResponse> openStore(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody StoreOpenRequest storeOpenRequest
    ) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return ResponseEntity.ok(storeService.openMyStore(userId, storeOpenRequest.getOpen()));
    }

    @Operation(
            summary = "상점 상세 조회",
            description = """
                    특정 상점의 상세 정보를 조회합니다.
                    - 인증: Consumer
                    - 요청: storeId
                    - 응답: 상점 상세 정보 (id, name, market, open, favorite, products, deals 등)
                    """
    )
    @GetMapping("/{storeId}/detail")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<StoreDetailDto> getStoreDetail(
            @PathVariable Long storeId,
            @AuthenticationPrincipal Long userId
    ) {
        Long consumerId = consumerRepository.findByUser_Id(userId)
                .map(Consumer::getId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONSUMER_NOT_FOUND));

        StoreDetailDto dto = storeQueryService.getStoreDetail(storeId, consumerId);

        recentStoreService.add(consumerId, storeId); // 최근 본 상점 기록

        return ResponseEntity.ok(dto);
    }
}