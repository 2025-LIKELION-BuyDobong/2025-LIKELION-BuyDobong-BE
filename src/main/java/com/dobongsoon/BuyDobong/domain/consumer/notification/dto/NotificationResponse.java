package com.dobongsoon.BuyDobong.domain.consumer.notification.dto;

import com.dobongsoon.BuyDobong.domain.consumer.notification.model.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String title;
    private String body;
    private LocalDateTime createdAt;
}