package com.dobongsoon.BuyDobong.domain.consumer.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PushPreferenceResponse {
    private boolean pushEnabled;
}