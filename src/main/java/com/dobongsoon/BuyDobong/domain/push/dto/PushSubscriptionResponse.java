package com.dobongsoon.BuyDobong.domain.push.dto;

import com.dobongsoon.BuyDobong.domain.push.model.PushSubscription;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PushSubscriptionResponse {
    private Long id;
    private String endpoint;;
    private LocalDateTime createdAt;

    public static PushSubscriptionResponse from(PushSubscription s) {
        return PushSubscriptionResponse.builder()
                .id(s.getId())
                .endpoint(s.getEndpoint())
                .createdAt(s.getCreatedAt())
                .build();
    }
}