package com.dobongsoon.BuyDobong.domain.store.service;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.favorite.repository.FavoriteStoreRepository;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductDto;
import com.dobongsoon.BuyDobong.domain.product.model.Product;
import com.dobongsoon.BuyDobong.domain.product.repository.ProductRepository;
import com.dobongsoon.BuyDobong.domain.store.dto.RandomStoreResponse;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreDetailDto;
import com.dobongsoon.BuyDobong.domain.store.model.Store;
import com.dobongsoon.BuyDobong.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreQueryService {

    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final FavoriteStoreRepository favoriteStoreRepository;

    @Transactional(readOnly = true)
    public List<RandomStoreResponse> getRandomStores(int size) {
        int n = Math.max(1, Math.min(size, 20)); // 1~20 가드
        return storeRepository.findRandom(n).stream()
                .map(RandomStoreResponse::from)
                .toList();
    }

    public StoreDetailDto getStoreDetail(Long storeId, Long userId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        List<Product> _products = productRepository.findByStore_IdAndHiddenFalse(storeId);

        LocalDateTime now = LocalDateTime.now();

        List<ProductDto> all = _products.stream()
                .map(product -> ProductDto.from(product, now))
                .toList();

        // 유니코드순 정렬
        Comparator<ProductDto> byName = Comparator.comparing(ProductDto::name);

        List<ProductDto> products = all.stream()
                .sorted(byName)
                .toList();

        List<ProductDto> deals = all.stream()
                .filter(ProductDto::dealActive)
                .sorted(byName)
                .toList();

        boolean favorite = false;
        if (userId != null) {
            favorite = favoriteStoreRepository.existsByUser_IdAndStoreId(userId, storeId);
        }

        return StoreDetailDto.of(store, favorite, deals, products);
    }
}