package com.dobongsoon.BuyDobong.domain.store.dto;

import com.dobongsoon.BuyDobong.domain.store.model.Store;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RandomStoreResponse {
    private Long id;
    private String name;
    private String market;
    private String imageUrl;
    private boolean open;

    public static RandomStoreResponse from(Store s) {
        return RandomStoreResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .market(s.getMarket().getLabel())
                .imageUrl(s.getImageUrl())
                .open(s.isOpen())
                .build();
    }
}
