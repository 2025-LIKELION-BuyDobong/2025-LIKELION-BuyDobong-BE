package com.dobongsoon.BuyDobong.domain.product.dto;

import com.dobongsoon.BuyDobong.domain.product.model.StockLevel;
import lombok.*;

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
    private String unit;
    private StockLevel stockLevel;
}
