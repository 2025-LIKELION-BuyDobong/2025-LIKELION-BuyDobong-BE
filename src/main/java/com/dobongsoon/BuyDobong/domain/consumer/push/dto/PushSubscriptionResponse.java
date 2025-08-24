package com.dobongsoon.BuyDobong.domain.consumer.push.dto;

import com.dobongsoon.BuyDobong.domain.consumer.push.model.PushSubscription;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PushSubscriptionResponse {
    private Long id;
    private String endpoint;
    private boolean active;
    private LocalDateTime createdAt;

    public static PushSubscriptionResponse from(PushSubscription s) {
        return PushSubscriptionResponse.builder()
                .id(s.getId())
                .endpoint(s.getEndpoint())
                .active(s.isActive())
                .createdAt(s.getCreatedAt())
                .build();
    }
}