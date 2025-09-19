package com.dobongsoon.BuyDobong.domain.recent.controller;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.recent.dto.RecentStoreResponse;
import com.dobongsoon.BuyDobong.domain.recent.model.RecentStore;
import com.dobongsoon.BuyDobong.domain.recent.service.RecentStoreService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recent")
public class RecentStoreController {

    private final RecentStoreService recentStoreService;

    @Operation(
            summary = "최근 본 상점 기록/갱신",
            description = """
            사용자가 상점을 방문했을 때 최근 본 상점 목록에 기록하거나,
            이미 존재하면 viewedAt 시간을 갱신합니다.
            - 인증 필요
            - 요청: storeId (PathVariable)
            - 응답: 200 OK
            """
    )
    @PostMapping("/{storeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> add(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long storeId
    ) {
        if (userId == null) throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        recentStoreService.add(userId, storeId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "최근 본 상점 Top 5 조회",
            description = """
            사용자가 최근에 본 상점 목록을 최신순으로 최대 5개 조회합니다.
            - 인증 필요
            - 응답: 상점 리스트 (id, name, market, imageUrl, open, viewedAt)
            """
    )
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RecentStoreResponse>> list(
            @AuthenticationPrincipal Long userId
    ) {
        if (userId == null) throw new BusinessException(ErrorCode.USER_NOT_FOUND);

        List<RecentStore> rows = recentStoreService.list(userId);
        List<RecentStoreResponse> body = rows.stream()
                .map(r -> RecentStoreResponse.builder()
                        .id(r.getStore().getId())
                        .name(r.getStore().getName())
                        .market(r.getStore().getMarket().name())
                        .imageUrl(r.getStore().getImageUrl())
                        .open(r.getStore().isOpen())
                        .viewedAt(r.getViewedAt())
                        .build())
                .toList();

        return ResponseEntity.ok(body);
    }

    @Operation(
            summary = "최근 본 상점 삭제",
            description = """
            사용자가 최근 본 상점 목록에서 개별 상점을 삭제합니다.
            - 인증 필요
            - 요청: storeId (PathVariable)
            - 응답: 204 No Content
            """
    )
    @DeleteMapping("/{storeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> remove(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long storeId
    ) {
        if (userId == null) throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        recentStoreService.remove(userId, storeId);
        return ResponseEntity.noContent().build();
    }
}