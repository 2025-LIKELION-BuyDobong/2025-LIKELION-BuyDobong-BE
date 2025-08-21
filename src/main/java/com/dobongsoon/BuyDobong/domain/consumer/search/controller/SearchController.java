package com.dobongsoon.BuyDobong.domain.consumer.search.controller;

import com.dobongsoon.BuyDobong.domain.consumer.search.dto.SearchResponse;
import com.dobongsoon.BuyDobong.domain.consumer.search.service.SearchService;
import com.dobongsoon.BuyDobong.domain.consumer.service.ConsumerKeywordService;
import com.dobongsoon.BuyDobong.domain.store.model.MarketName;
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