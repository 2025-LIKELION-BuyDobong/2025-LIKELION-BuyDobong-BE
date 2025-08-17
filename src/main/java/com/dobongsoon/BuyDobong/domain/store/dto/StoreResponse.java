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
    private String address;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
    private boolean open;
    private LocalDateTime createdAt;

    public static StoreResponse from(Store store) {
        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .address(store.getAddress())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .imageUrl(store.getImageUrl())
                .open(store.isOpen())
                .createdAt(store.getCreatedAt())
                .build();
    }
}
