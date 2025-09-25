package com.dobongsoon.BuyDobong.domain.search.service;

import com.dobongsoon.BuyDobong.domain.keyword.service.UserKeywordService;
import com.dobongsoon.BuyDobong.domain.product.model.Product;
import com.dobongsoon.BuyDobong.domain.product.repository.ProductRepository;
import com.dobongsoon.BuyDobong.domain.search.dto.SearchResponse;
import com.dobongsoon.BuyDobong.domain.store.model.MarketName;
import com.dobongsoon.BuyDobong.domain.store.model.Store;
import com.dobongsoon.BuyDobong.domain.store.repository.StoreRepository;
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

    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final UserKeywordService userKeywordService;

    public List<SearchResponse> search(Long userId,
                                       String query,
                                       List<MarketName> markets,
                                       boolean onlyDeal) {
        String q = query == null ? "" : query.trim();
        if (q.isEmpty()) return List.of();

        List<MarketName> marketFilter = (markets == null || markets.isEmpty()) ? null : markets;

        // 1. 상품 기준 검색
        List<Product> rows = productRepository.search(q, marketFilter);
        var now = java.time.LocalDateTime.now();
        if (onlyDeal) {
            rows = rows.stream().filter(p -> p.isDealActive(now)).toList();
        }

        boolean interested = (userId != null) && userKeywordService.isInterested(userId, q);

        // storeId -> products 매핑
        Map<Long, List<Product>> byStoreId = rows.stream()
                .collect(Collectors.groupingBy(p -> p.getStore().getId(),
                        LinkedHashMap::new, Collectors.toList()));

        Comparator<Product> byProductName = Comparator.comparing(Product::getName);
        List<SearchResponse> productSide = byStoreId.entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getValue().get(0).getStore().getName()))
                .map(entry -> {
                    Store s = entry.getValue().get(0).getStore();
                    List<SearchResponse.ProductDto> products = entry.getValue().stream()
                            .sorted(byProductName)
                            .map(p -> SearchResponse.ProductDto.builder()
                                    .id(p.getId())
                                    .name(p.getName())
                                    .displayPrice(p.getDisplayPrice(now))
                                    .displayUnit(p.getDisplayUnit(now))
                                    .dealActive(p.isDealActive(now))
                                    .dealStartAt(p.getDealStartAt())
                                    .dealEndAt(p.getDealEndAt())
                                    .build())
                            .toList();

                    return SearchResponse.builder()
                            .store(SearchResponse.StoreDto.builder()
                                    .id(s.getId())
                                    .name(s.getName())
                                    .market(s.getMarket().name())
                                    .marketLabel(s.getMarket().getLabel())
                                    .imageUrl(s.getImageUrl())
                                    .open(s.isOpen())
                                    .build())
                            .products(products)
                            .interested(interested)
                            .build();
                })
                .toList();

        // 2. 상점명 검색 (상품 없어도 포함)
        List<Store> storesByName = storeRepository.searchByName(q, marketFilter);

        // 3. 상품 결과에 이미 포함된 상점 제외하고, 빈 상품 리스트로 추가
        var included = byStoreId.keySet();
        List<SearchResponse> storeOnly = storesByName.stream()
                .filter(s -> !included.contains(s.getId()))
                .sorted(Comparator.comparing(Store::getName))
                .map(s -> SearchResponse.builder()
                        .store(SearchResponse.StoreDto.builder()
                                .id(s.getId())
                                .name(s.getName())
                                .market(s.getMarket().name())
                                .marketLabel(s.getMarket().getLabel())
                                .imageUrl(s.getImageUrl())
                                .open(s.isOpen())
                                .build())
                        .products(List.of())   // 상품 없음
                        .interested(interested)
                        .build())
                .toList();

        // 합치기
        List<SearchResponse> merged = new java.util.ArrayList<SearchResponse>(productSide.size() + storeOnly.size());
        merged.addAll(productSide);
        merged.addAll(storeOnly);

        return merged;
    }
}