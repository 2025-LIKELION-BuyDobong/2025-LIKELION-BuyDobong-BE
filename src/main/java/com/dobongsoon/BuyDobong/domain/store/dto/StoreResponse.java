package com.dobongsoon.BuyDobong.domain.store.dto;

import com.dobongsoon.BuyDobong.domain.store.model.Store;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class StoreResponse {
    private Long id;
    private String name;
    private String market;      // ex) SINDOBONT
    private String marketLabel; // ex) 신도봉시장
    private Double latitude;
    private Double longitude;
    private String imageUrl;
    private boolean open;
    private LocalDateTime createdAt;

    public static StoreResponse from(Store store) {
        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .market(store.getMarket().name())
                .marketLabel(store.getMarket().getLabel())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .imageUrl(store.getImageUrl())
                .open(store.isOpen())
                .createdAt(store.getCreatedAt())
                .build();
    }
}
