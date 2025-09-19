package com.dobongsoon.BuyDobong.domain.favorite.controller;

import com.dobongsoon.BuyDobong.domain.favorite.dto.FavoriteStoreRequest;
import com.dobongsoon.BuyDobong.domain.favorite.dto.FavoriteStoreResponse;
import com.dobongsoon.BuyDobong.domain.favorite.service.FavoriteStoreService;
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
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteStoreController {

    private final FavoriteStoreService favoriteStoreService;

    @Operation(
            summary = "관심 상점 등록",
            description = """
        사용자가 상점을 관심 상점으로 등록합니다.
        - 인증 필요
        - 요청: storeId
        - 응답: 등록된 상점 정보 (id, name, market, isOpen, createdAt)
        """
    )
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FavoriteStoreResponse> addFavorite(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid FavoriteStoreRequest request
    ) {
        FavoriteStoreResponse body = favoriteStoreService.addFavorite(userId, request.getStoreId());
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }


    @Operation(
            summary = "관심 상점 조회",
            description = """
        사용자의 관심 상점 목록을 조회합니다.
        - 인증 필요
        - 요청: storeId
        - 응답: 조회된 상점 목록과 정보 (id, name, market, imageUrl, isOpen, createdAt)
        """
    )
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FavoriteStoreResponse>> getFavorites(
            @AuthenticationPrincipal Long userId
    ) {
        List<FavoriteStoreResponse> favorites = favoriteStoreService.getFavorites(userId);
        return ResponseEntity.ok(favorites);
    }

    @Operation(
            summary = "관심 상점 해제",
            description = """
        사용자가 등록한 관심 상점을 해제합니다.
        - 인증 필요
        - 요청: storeId
        - 응답: 성공 시 204 No Content
        """
    )
    @DeleteMapping("/{storeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal Long userId,
            @PathVariable("storeId") Long storeId
    ) {
        favoriteStoreService.removeFavorite(userId, storeId);
        return ResponseEntity.noContent().build();
    }
}