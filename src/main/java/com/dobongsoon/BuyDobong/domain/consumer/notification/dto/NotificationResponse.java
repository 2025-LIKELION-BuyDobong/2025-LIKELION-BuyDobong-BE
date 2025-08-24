package com.dobongsoon.BuyDobong.domain.consumer.notification.dto;

import com.dobongsoon.BuyDobong.domain.consumer.notification.model.Notification;
import com.dobongsoon.BuyDobong.domain.consumer.notification.model.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {
    private Long id;
    private Long consumerId;
    private NotificationType type;
    private String title;
    private String body;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .consumerId(n.getConsumer().getId())
                .type(n.getType())
                .title(n.getTitle())
                .body(n.getBody())
                .createdAt(n.getCreatedAt())
                .build();
    }
}