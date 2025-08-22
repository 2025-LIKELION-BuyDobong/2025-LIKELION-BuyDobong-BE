package com.dobongsoon.BuyDobong.domain.consumer.recent.controller;

import com.dobongsoon.BuyDobong.domain.consumer.recent.dto.RecentStoreResponse;
import com.dobongsoon.BuyDobong.domain.consumer.recent.model.RecentStore;
import com.dobongsoon.BuyDobong.domain.consumer.recent.service.RecentStoreService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/consumer/{consumerId}/recent")
public class RecentStoreController {

    private final RecentStoreService recentStoreService;

    @Operation(
            summary = "최근 본 상점 기록/갱신",
            description = """
            특정 소비자가 상점을 방문했을 때 최근 본 상점 목록에 기록하거나,
            이미 존재하면 viewedAt 시간을 갱신합니다.
            - 인증 필요: CONSUMER
            - 요청: storeId (PathVariable)
            - 응답: 200 OK
            """
    )
    @PostMapping("/{storeId}")
    public ResponseEntity<Void> add(@PathVariable Long consumerId, @PathVariable Long storeId) {
        recentStoreService.add(consumerId, storeId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "최근 본 상점 Top 5 조회",
            description = """
            특정 소비자가 최근에 본 상점 목록을 최신순으로 최대 5개 조회합니다.
            - 인증 필요: CONSUMER
            - 응답: 상점 리스트 (id, name, market, open, viewedAt)
            """
    )
    @GetMapping
    public ResponseEntity<List<RecentStoreResponse>> list(@PathVariable Long consumerId) {
        List<RecentStore> rows = recentStoreService.list(consumerId);
        List<RecentStoreResponse> body = rows.stream()
                .map(r -> RecentStoreResponse.builder()
                        .id(r.getStore().getId())
                        .name(r.getStore().getName())
                        .market(r.getStore().getMarket().name())
                        .open(r.getStore().isOpen())
                        .viewedAt(r.getViewedAt())
                        .build())
                .toList();
        return ResponseEntity.ok(body);
    }

    @Operation(
            summary = "최근 본 상점 삭제",
            description = """
            특정 소비자가 최근 본 상점 목록에서 개별 상점을 삭제합니다.
            - 인증 필요: CONSUMER
            - 요청: storeId (PathVariable)
            - 응답: 204 No Content
            """
    )
    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> remove(@PathVariable Long consumerId, @PathVariable Long storeId) {
        recentStoreService.remove(consumerId, storeId);
        return ResponseEntity.noContent().build();
    }
}