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
public class ProductDealUpdateRequest {
    @Min(0)
    private Long dealPrice;

    private String dealUnit;

    private LocalDateTime dealStartAt;

    private LocalDateTime dealEndAt;
}
