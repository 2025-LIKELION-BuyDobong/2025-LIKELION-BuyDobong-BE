package com.dobongsoon.BuyDobong.domain.store.dto;

import com.dobongsoon.BuyDobong.domain.store.model.MarketName;
import com.dobongsoon.BuyDobong.domain.store.model.Store;

import lombok.Builder;

@Builder
public record StoreSummaryDto(
        Long id,
        String name,
        String market,
        String marketLabel,
        boolean open,
        String imageUrl
) {
    public static StoreSummaryDto from(Store store) {
        return StoreSummaryDto.builder()
                .id(store.getId())
                .name(store.getName())
                .market(store.getMarket().name())
                .marketLabel(store.getMarket().getLabel())
                .open(store.isOpen())
                .imageUrl(store.getImageUrl())
                .build();
    }
}