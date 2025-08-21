package com.dobongsoon.BuyDobong.domain.consumer.recent.controller;

import com.dobongsoon.BuyDobong.domain.consumer.recent.dto.RecentStoreResponse;
import com.dobongsoon.BuyDobong.domain.consumer.recent.model.RecentStore;
import com.dobongsoon.BuyDobong.domain.consumer.recent.service.RecentStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/consumer/{consumerId}/recent")
public class RecentStoreController {

    private final RecentStoreService recentStoreService;

    // 최근 본 상점 기록/갱신
    @PostMapping("/{storeId}")
    public ResponseEntity<Void> add(@PathVariable Long consumerId, @PathVariable Long storeId) {
        recentStoreService.add(consumerId, storeId);
        return ResponseEntity.ok().build();
    }

    // 최근 본 상점 Top 5 조회
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

    // 최근 본 상점 삭제
    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> remove(@PathVariable Long consumerId, @PathVariable Long storeId) {
        recentStoreService.remove(consumerId, storeId);
        return ResponseEntity.noContent().build();
    }
}