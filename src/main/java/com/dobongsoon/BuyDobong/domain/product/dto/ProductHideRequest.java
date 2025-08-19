package com.dobongsoon.BuyDobong.domain.product.dto;


import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductHideRequest {
    @NotNull
    private Boolean hidden;
}
