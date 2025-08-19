package com.dobongsoon.BuyDobong.domain.search.controller;

import com.dobongsoon.BuyDobong.domain.search.dto.SearchResponse;
import com.dobongsoon.BuyDobong.domain.search.service.SearchService;
import com.dobongsoon.BuyDobong.domain.store.model.MarketName;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SearchController {

    private final SearchService searchService;

    /**
     * 예) /api/search?query=케이크
     *     /api/search?query=케이크&onlyDeal=true
     *     /api/search?query=케이크&markets=SINDOBONG,BAEGUN
     *     /api/search?query=케이크&markets=SINDOBONG&markets=BAEGUN
     */
    @GetMapping("/search")
    public ResponseEntity<List<SearchResponse>> search(
            @RequestParam String query,
            @RequestParam(required = false) List<MarketName> markets,
            @RequestParam(required = false, defaultValue = "false") boolean onlyDeal
    ) {
        return ResponseEntity.ok(searchService.search(query, markets, onlyDeal));
    }
}