package com.dobongsoon.BuyDobong.domain.consumer.push.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PushSubscriptionRequest {
    @NotBlank private String endpoint;
    @NotBlank private String p256dh;
    @NotBlank private String auth;
}