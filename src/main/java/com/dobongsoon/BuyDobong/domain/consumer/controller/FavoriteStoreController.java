package com.dobongsoon.BuyDobong.domain.consumer.controller;

import com.dobongsoon.BuyDobong.domain.consumer.dto.FavoriteStoreRequest;
import com.dobongsoon.BuyDobong.domain.consumer.model.FavoriteStore;
import com.dobongsoon.BuyDobong.domain.consumer.dto.FavoriteStoreResponse;
import com.dobongsoon.BuyDobong.domain.consumer.service.FavoriteStoreService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consumer/{consumerId}/favorite")
@RequiredArgsConstructor
public class FavoriteStoreController {

    private final FavoriteStoreService favoriteStoreService;

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
    public ResponseEntity<FavoriteStoreResponse> addFavorite(
            @PathVariable("consumerId") Long consumerId,
            @RequestBody @Valid FavoriteStoreRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(favoriteStoreService.addFavorite(consumerId, request.getStoreId()));
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
    @GetMapping
    public ResponseEntity<List<FavoriteStoreResponse>> getFavorites(@PathVariable Long consumerId) {
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
    public ResponseEntity<Void> removeFavorite(
            @PathVariable("consumerId") Long consumerId,
            @PathVariable("storeId") Long storeId
    ) {
        favoriteStoreService.removeFavorite(consumerId, storeId);
        return ResponseEntity.noContent().build();
    }
}