package com.dobongsoon.BuyDobong.domain.consumer.controller;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.consumer.dto.FavoriteStoreRequest;
import com.dobongsoon.BuyDobong.domain.consumer.model.Consumer;
import com.dobongsoon.BuyDobong.domain.consumer.dto.FavoriteStoreResponse;
import com.dobongsoon.BuyDobong.domain.consumer.repository.ConsumerRepository;
import com.dobongsoon.BuyDobong.domain.consumer.service.FavoriteStoreService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consumer/favorite")
@RequiredArgsConstructor
public class FavoriteStoreController {

    private final FavoriteStoreService favoriteStoreService;
    private final ConsumerRepository consumerRepository;

    private Long consumerIdOrThrow(Long userId) {
        return consumerRepository.findByUser_Id(userId)
                .map(Consumer::getId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONSUMER_NOT_FOUND));
    }

    @Operation(
            summary = "관심 상점 등록",
            description = """
        특정 소비자가 상점을 관심 상점으로 등록합니다.
        - 인증 필요: CONSUMER
        - 요청: storeId
        - 응답: 등록된 상점 정보 (id, name, market, isOpen, createdAt)
        """
    )
    @PostMapping
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<FavoriteStoreResponse> addFavorite(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid FavoriteStoreRequest request
    ) {
        Long consumerId = consumerIdOrThrow(userId);
        FavoriteStoreResponse body = favoriteStoreService.addFavorite(consumerId, request.getStoreId());
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }


    @Operation(
            summary = "관심 상점 조회",
            description = """
        특정 소비자의 관심 상점 목록을 조회합니다.
        - 인증 필요: CONSUMER
        - 요청: storeId
        - 응답: 조회된 상점 목록과 정보 (id, name, market, imageUrl, isOpen, createdAt)
        """
    )
    @GetMapping
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<List<FavoriteStoreResponse>> getFavorites(
            @AuthenticationPrincipal Long userId
    ) {
        Long consumerId = consumerIdOrThrow(userId);
        List<FavoriteStoreResponse> favorites = favoriteStoreService.getFavorites(consumerId);
        return ResponseEntity.ok(favorites);
    }

    @Operation(
            summary = "관심 상점 해제",
            description = """
        특정 소비자가 등록한 관심 상점을 해제합니다.
        - 인증 필요: CONSUMER
        - 요청: storeId
        - 응답: 성공 시 204 No Content
        """
    )
    @DeleteMapping("/{storeId}")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal Long userId,
            @PathVariable("storeId") Long storeId
    ) {
        Long consumerId = consumerIdOrThrow(userId);
        favoriteStoreService.removeFavorite(consumerId, storeId);
        return ResponseEntity.noContent().build();
    }
}