package com.dobongsoon.BuyDobong.domain.consumer.search.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class SearchResponse {
    private StoreDto store;
    private List<ProductDto> products;
    private Boolean interested;

    @Getter
    @Builder
    public static class StoreDto {
        private Long id;
        private String name;
        private String market;
        private String marketLabel;
        private String imageUrl;
        private Boolean open;
    }

    @Getter
    @Builder
    public static class ProductDto {
        private Long id;
        private String name;
        private Long displayPrice;
        private String displayUnit;
        private boolean dealActive;
        private LocalDateTime dealStartAt;
        private LocalDateTime dealEndAt;
    }
}