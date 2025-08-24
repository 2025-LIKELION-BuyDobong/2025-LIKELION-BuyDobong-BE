package com.dobongsoon.BuyDobong.domain.consumer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PushPreferenceRequest {
    private boolean pushEnabled;
}