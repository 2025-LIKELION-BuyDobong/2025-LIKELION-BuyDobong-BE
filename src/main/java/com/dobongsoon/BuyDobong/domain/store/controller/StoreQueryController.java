package com.dobongsoon.BuyDobong.domain.store.controller;

import com.dobongsoon.BuyDobong.domain.recent.service.RecentStoreService;
import com.dobongsoon.BuyDobong.domain.store.dto.RandomStoreResponse;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreDetailDto;
import com.dobongsoon.BuyDobong.domain.store.service.StoreQueryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreQueryController {

    private final StoreQueryService storeQueryService;
    private final RecentStoreService recentStoreService;

    @GetMapping("/random")
    @Operation(
            summary = "랜덤 상점 조회",
            description = """
                    랜덤 상점 5개의 정보를 조회합니다.
                    - 인증 필요 X
                    - 응답: 상점 정보 (id, name, market, imageUrl, open)
                    """
    )
    public ResponseEntity<List<RandomStoreResponse>> randomStores(
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(storeQueryService.getRandomStores(size));
    }

    @Operation(
            summary = "상점 상세 조회",
            description = """
                    특정 상점의 상세 정보를 조회합니다.
                    - 인증 필요 X
                    - 요청: storeId
                    - 응답: 상점 상세 정보 (id, name, market, open, favorite, products, deals 등)
                    """
    )
    @GetMapping("/{storeId}/detail")
    public ResponseEntity<StoreDetailDto> getStoreDetail(
            @PathVariable Long storeId,
            @AuthenticationPrincipal Long userId
    ) {
        // userId가 null이어도 동작하도록 StoreQueryService가 처리 (관심 상점 설정 = false)
        StoreDetailDto dto = storeQueryService.getStoreDetail(storeId, userId);

        // 로그인한 경우에만 최근 본 상점 기록
        if (userId != null) {
            recentStoreService.add(userId, storeId);
        }

        return ResponseEntity.ok(dto);
    }
}
