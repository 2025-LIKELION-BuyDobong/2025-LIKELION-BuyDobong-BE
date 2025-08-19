package com.dobongsoon.BuyDobong.domain.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDealRequest {
    @NotNull
    @Min(0)
    private Long dealPrice;

    private String dealUnit;

    @NotNull
    private LocalDateTime dealStartAt;

    @NotNull
    private LocalDateTime dealEndAt;
}
