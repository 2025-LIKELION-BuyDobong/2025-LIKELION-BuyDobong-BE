package com.dobongsoon.BuyDobong.domain.store.dto;

import com.dobongsoon.BuyDobong.domain.product.dto.ProductDto;
import com.dobongsoon.BuyDobong.domain.store.model.Store;
import lombok.Builder;

import java.util.List;

@Builder
public record StoreDetailDto(
        Long id,
        String name,
        String market,
        String marketLabel,
        boolean open,
        boolean favorite,
        String imageUrl,
        List<ProductDto> deals,
        List<ProductDto> products
) {

    public static StoreDetailDto of(Store store, List<ProductDto> deals, List<ProductDto> products) {
        return of(store, false, deals, products); // 기본 false
    }

    public static StoreDetailDto of(Store store, boolean favorite, List<ProductDto> deals, List<ProductDto> products) {
        return StoreDetailDto.builder()
                .id(store.getId())
                .name(store.getName())
                .market(store.getMarket().name())
                .marketLabel(store.getMarket().getLabel())
                .open(store.isOpen())
                .favorite(favorite)
                .imageUrl(store.getImageUrl())
                .deals(deals)
                .products(products)
                .build();
    }
}