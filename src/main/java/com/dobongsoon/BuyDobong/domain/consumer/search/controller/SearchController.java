package com.dobongsoon.BuyDobong.domain.consumer.search.controller;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.consumer.model.Consumer;
import com.dobongsoon.BuyDobong.domain.consumer.repository.ConsumerRepository;
import com.dobongsoon.BuyDobong.domain.consumer.search.dto.SearchResponse;
import com.dobongsoon.BuyDobong.domain.consumer.search.service.SearchService;
import com.dobongsoon.BuyDobong.domain.store.model.MarketName;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/consumer/search")
public class SearchController {

    private final SearchService searchService;
    private final ConsumerRepository consumerRepository;

    /**
     * 예) /api/consumer/search?query=케이크
     *     /api/consumer/search?query=케이크&onlyDeal=true
     *     /api/consumer/search?query=케이크&markets=SINDOBONG,BAEGUN
     *     /api/consumer/search?query=케이크&markets=SINDOBONG&markets=BAEGUN
     */
    @Operation(
            summary = "상점/상품 검색 및 조회",
            description = """
            특정 소비자가 키워드로 상점/상품을 검색하고 조회합니다.
            - 인증 필요: CONSUMER
            - 요청 파라미터:
              - query: 검색어 (필수)
              - markets: 시장 이름 목록 (선택, 0~n개 가능)
              - onlyDeal: 특가 상품만 조회 여부 (기본값: false)
            - 응답: 상점/상품 리스트 (id, name, price, market, deal 여부 등)
            
            ✅ interested: 관심 키워드 여부
            -> 상점마다 중복으로 나타나는데... 하나만 나타나게 하기엔 조금 복잡해서 그냥 뒀어요
               중복으로 계속 뜰 뿐, 여부는 정상 작동하니까 잘 갖다 쓰시면 될 것 같습니당 ^_^;;
            """
    )
    @GetMapping
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<List<SearchResponse>> search(
            @AuthenticationPrincipal Long userId,
            @RequestParam String query,
            @RequestParam(required = false) List<MarketName> markets,
            @RequestParam(required = false, defaultValue = "false") boolean onlyDeal
    ) {
        Long consumerId = consumerRepository.findByUser_Id(userId)
                .map(Consumer::getId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONSUMER_NOT_FOUND));

        List<SearchResponse> result = searchService.search(consumerId, query, markets, onlyDeal);
        return ResponseEntity.ok(result);
    }
}