package com.dobongsoon.BuyDobong.domain.product.dto;

import com.dobongsoon.BuyDobong.domain.product.model.StockLevel;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private Long storeId;
    private String name;
    private Long regularPrice;
    private String regularUnit;
    private StockLevel stockLevel;
    private LocalDateTime createdAt;

    // 특가
    private Long dealPrice;
    private String dealUnit;
    private LocalDateTime dealStartAt;
    private LocalDateTime dealEndAt;

    private Long displayPrice;
    private String displayUnit;

    private boolean hidden;
}
