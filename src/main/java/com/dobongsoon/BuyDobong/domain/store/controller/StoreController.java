package com.dobongsoon.BuyDobong.domain.store.controller;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.common.s3.S3Service;
import com.dobongsoon.BuyDobong.domain.consumer.recent.service.RecentStoreService;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreCreateRequest;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreDetailDto;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreOpenRequest;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreResponse;
import com.dobongsoon.BuyDobong.domain.store.service.StoreQueryService;
import com.dobongsoon.BuyDobong.domain.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    private final S3Service s3Service;

    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('MERCHANT')")
    @Operation(summary = "상점 이미지 업로드")
    public ResponseEntity<String> uploadStoreImage(
            @AuthenticationPrincipal Long userId,
            @RequestPart("file") MultipartFile file
    ) {
        String url = s3Service.uploadStoreImage(userId, file);
        return ResponseEntity.ok(url);
    }

    @PostMapping
    @PreAuthorize("hasRole('MERCHANT')")
    @Operation(summary = "상점 등록")
    public ResponseEntity<StoreResponse> createStore(
            @AuthenticationPrincipal Long userId,
            Authentication authentication,
            @Valid @RequestBody StoreCreateRequest storeCreateRequest
    ) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(storeService.register(userId, storeCreateRequest));
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

    // 상점 상세 조회
    @GetMapping("/{storeId}/detail/{consumerId}")
    public ResponseEntity<StoreDetailDto> getStoreDetail(
            @PathVariable Long storeId,
            @PathVariable(required = false) Long consumerId
    ) {
        StoreDetailDto dto = storeQueryService.getStoreDetail(storeId, consumerId);

        if (consumerId != null) {
            recentStoreService.add(consumerId, storeId);
        }

        return ResponseEntity.ok(dto);
    }
}
