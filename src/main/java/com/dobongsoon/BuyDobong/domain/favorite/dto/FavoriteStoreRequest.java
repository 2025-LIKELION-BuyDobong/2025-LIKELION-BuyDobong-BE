package com.dobongsoon.BuyDobong.domain.favorite.dto;

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