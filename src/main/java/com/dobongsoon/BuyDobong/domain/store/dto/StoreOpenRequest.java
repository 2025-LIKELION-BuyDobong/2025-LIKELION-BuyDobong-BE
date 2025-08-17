package com.dobongsoon.BuyDobong.domain.store.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
public class StoreOpenRequest {
    @NotNull
    private Boolean open;
}
