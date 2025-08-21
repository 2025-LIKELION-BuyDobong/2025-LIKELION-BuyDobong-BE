package com.dobongsoon.BuyDobong.domain.consumer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteStoreRequest {

    @NotNull
    private Long storeId;
}