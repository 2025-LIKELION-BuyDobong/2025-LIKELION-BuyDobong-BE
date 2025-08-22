package com.dobongsoon.BuyDobong.domain.consumer.search.controller;

import com.dobongsoon.BuyDobong.domain.consumer.search.dto.SearchResponse;
import com.dobongsoon.BuyDobong.domain.consumer.search.service.SearchService;
import com.dobongsoon.BuyDobong.domain.consumer.service.ConsumerKeywordService;
import com.dobongsoon.BuyDobong.domain.store.model.MarketName;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/consumer/{consumerId}/search")
public class SearchController {

    private final SearchService searchService;
    private final ConsumerKeywordService consumerKeywordService;

    /**
     * 예) /api/consumer/1/search?query=케이크
     *     /api/consumer/1/search?query=케이크&onlyDeal=true
     *     /api/consumer/1/search?query=케이크&markets=SINDOBONG,BAEGUN
     *     /api/consumer/1/search?query=케이크&markets=SINDOBONG&markets=BAEGUN
     */
    @Operation(
            summary = "상품 검색",
            description = """
            특정 소비자가 키워드로 상품을 검색합니다.
            - 인증 필요: CONSUMER
            - 요청 파라미터:
              - query: 검색어 (필수)
              - markets: 시장 이름 목록 (선택, 0~n개 가능)
              - onlyDeal: 특가 상품만 조회 여부 (기본값: false)
            - 응답: 상품 리스트 (id, name, price, market, deal 여부 등)
            """
    )
    @GetMapping
    public ResponseEntity<List<SearchResponse>> search(
            @PathVariable Long consumerId,
            @RequestParam String query,
            @RequestParam(required = false) List<MarketName> markets,
            @RequestParam(required = false, defaultValue = "false") boolean onlyDeal
    ) {
        List<SearchResponse> result = searchService.search(consumerId, query, markets, onlyDeal);
        return ResponseEntity.ok(result);
    }
}