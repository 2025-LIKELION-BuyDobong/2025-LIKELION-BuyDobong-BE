package com.dobongsoon.BuyDobong.domain.search.service;

import com.dobongsoon.BuyDobong.domain.keyword.service.ConsumerKeywordService;
import com.dobongsoon.BuyDobong.domain.product.model.Product;
import com.dobongsoon.BuyDobong.domain.product.repository.ProductRepository;
import com.dobongsoon.BuyDobong.domain.search.dto.SearchResponse;
import com.dobongsoon.BuyDobong.domain.store.model.MarketName;
import com.dobongsoon.BuyDobong.domain.store.model.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final ProductRepository productRepository;
    private final ConsumerKeywordService consumerKeywordService;

    public List<SearchResponse> search(Long consumerId,
                                       String query,
                                       List<MarketName> markets,
                                       boolean onlyDeal) {

        String q = query == null ? "" : query.trim();
        if (q.isEmpty()) return List.of();

        // 파라미터 정규화(빈 리스트 → null 로 JPQL 조건 통과)
        List<MarketName> marketFilter = (markets == null || markets.isEmpty()) ? null : markets;

        // 데이터 조회
        List<Product> rows = productRepository.search(q, marketFilter);

        LocalDateTime now = LocalDateTime.now();

        // onlyDeal=true면 특가만 남김
        List<Product> filtered = rows.stream()
                .filter(p -> !onlyDeal || p.isDealActive(now))
                .toList();

        // 관심 키워드 여부 (검색어 기준)
        boolean interested = (consumerId != null) && consumerKeywordService.isInterested(consumerId, q);

        // 스토어 ID 기준 그룹핑 (엔티티를 키로 안 쓰는 이유: 프록시/equals 이슈 회피)
        Map<Long, List<Product>> byStoreId = filtered.stream()
                .collect(Collectors.groupingBy(p -> p.getStore().getId(),
                        LinkedHashMap::new, Collectors.toList()));

        // 정렬 기준
        Comparator<Product> byProductName = Comparator.comparing(Product::getName);
        Comparator<Map.Entry<Long, List<Product>>> byStoreName =
                Comparator.comparing(e -> e.getValue().get(0).getStore().getName());

        // DTO 매핑
        return byStoreId.entrySet().stream()
                .sorted(byStoreName) // 상점명 ㄱㄴㄷ 순
                .map(entry -> {
                    List<Product> ps = entry.getValue();
                    Store s = ps.get(0).getStore();

                    List<SearchResponse.ProductDto> productDtos = ps.stream()
                            .sorted(byProductName) // 상품명 ㄱㄴㄷ 순
                            .map(p -> {
                                boolean dealActive = p.isDealActive(now);
                                Long displayPrice = p.getDisplayPrice(now);
                                String displayUnit = p.getDisplayUnit(now);

                                return SearchResponse.ProductDto.builder()
                                        .id(p.getId())
                                        .name(p.getName())
                                        .displayPrice(displayPrice)
                                        .displayUnit(displayUnit)
                                        .dealActive(dealActive)
                                        .dealStartAt(p.getDealStartAt())
                                        .dealEndAt(p.getDealEndAt())
                                        .build();
                            })
                            .toList();

                    SearchResponse.StoreDto storeDto = SearchResponse.StoreDto.builder()
                            .id(s.getId())
                            .name(s.getName())
                            .market(s.getMarket().name())
                            .marketLabel(s.getMarket().getLabel())
                            .imageUrl(s.getImageUrl())
                            .open(s.isOpen())
                            .build();

                    return SearchResponse.builder()
                            .store(storeDto)
                            .products(productDtos)
                            .interested(interested)
                            .build();
                })
                .toList();
    }
}