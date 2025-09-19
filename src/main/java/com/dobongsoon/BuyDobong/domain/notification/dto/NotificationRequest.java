package com.dobongsoon.BuyDobong.domain.notification.dto;

import com.dobongsoon.BuyDobong.domain.notification.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    private NotificationType type; // KEYWORD, STORE
    private String title;
    private String body;

    // 키워드 알림용
    private String keyword;
    private String productName;

    // 상점 알림용
    private String storeName;
}