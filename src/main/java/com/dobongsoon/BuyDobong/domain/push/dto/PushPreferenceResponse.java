package com.dobongsoon.BuyDobong.domain.push.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PushPreferenceResponse {
    private boolean pushEnabled;
}