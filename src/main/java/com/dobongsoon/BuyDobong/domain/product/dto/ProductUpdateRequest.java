package com.dobongsoon.BuyDobong.domain.product.dto;

import com.dobongsoon.BuyDobong.domain.product.model.StockLevel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {

    private String name;

    @Min(0)
    private Long regularPrice;

    private String regularUnit;

    private StockLevel stockLevel;
}
