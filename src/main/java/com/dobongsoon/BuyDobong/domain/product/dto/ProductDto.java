package com.dobongsoon.BuyDobong.domain.product.dto;

import com.dobongsoon.BuyDobong.domain.product.model.Product;
import com.dobongsoon.BuyDobong.domain.product.model.StockLevel;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProductDto(
        Long id,
        String name,
        Long displayPrice,
        String unit,
        StockLevel stockLevel,
        boolean dealActive,
        LocalDateTime dealStartAt,
        LocalDateTime dealEndAt
) {
    public static ProductDto from(Product p, LocalDateTime now) {
        return ProductDto.builder()
                .id(p.getId())
                .name(p.getName())
                .displayPrice(p.getDisplayPrice(now))
                .unit(p.getDisplayUnit(now))
                .stockLevel(p.getStockLevel())
                .dealActive(p.isDealActive(now))
                .dealStartAt(p.getDealStartAt())
                .dealEndAt(p.getDealEndAt())
                .build();
    }
}