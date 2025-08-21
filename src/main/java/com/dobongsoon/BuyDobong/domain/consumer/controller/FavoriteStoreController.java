package com.dobongsoon.BuyDobong.domain.consumer.controller;

import com.dobongsoon.BuyDobong.domain.consumer.dto.FavoriteStoreRequest;
import com.dobongsoon.BuyDobong.domain.consumer.model.FavoriteStore;
import com.dobongsoon.BuyDobong.domain.consumer.dto.FavoriteStoreResponse;
import com.dobongsoon.BuyDobong.domain.consumer.service.FavoriteStoreService;
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

    // 관심 상점 등록
    @PostMapping
    public ResponseEntity<FavoriteStoreResponse> addFavorite(
            @PathVariable("consumerId") Long consumerId,
            @RequestBody @Valid FavoriteStoreRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(favoriteStoreService.addFavorite(consumerId, request.getStoreId()));
    }

    // 관심 상점 목록 조회
    @GetMapping
    public ResponseEntity<List<FavoriteStoreResponse>> getFavorites(@PathVariable Long consumerId) {
        List<FavoriteStoreResponse> favorites = favoriteStoreService.getFavorites(consumerId);
        return ResponseEntity.ok(favorites);
    }

    // 관심 상점 해제
    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable("consumerId") Long consumerId,
            @PathVariable("storeId") Long storeId
    ) {
        favoriteStoreService.removeFavorite(consumerId, storeId);
        return ResponseEntity.noContent().build();
    }
}