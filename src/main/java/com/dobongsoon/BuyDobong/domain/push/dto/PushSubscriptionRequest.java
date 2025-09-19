package com.dobongsoon.BuyDobong.domain.push.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PushSubscriptionRequest {
    @NotBlank
    private String endpoint;

    @NotBlank
    @JsonProperty("p256dh")
    private String p256dh;

    @NotBlank
    private String auth;
}