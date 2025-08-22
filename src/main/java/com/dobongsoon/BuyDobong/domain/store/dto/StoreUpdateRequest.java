package com.dobongsoon.BuyDobong.domain.store.dto;

import com.dobongsoon.BuyDobong.domain.store.model.MarketName;
import lombok.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreUpdateRequest {

    private String name;

    private MarketName market;

    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double latitude;

    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double longitude;

    private String imageUrl;
}
