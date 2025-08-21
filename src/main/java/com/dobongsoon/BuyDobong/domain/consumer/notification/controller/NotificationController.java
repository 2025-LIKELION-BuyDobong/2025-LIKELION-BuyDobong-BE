package com.dobongsoon.BuyDobong.domain.consumer.notification.controller;

import com.dobongsoon.BuyDobong.domain.consumer.notification.dto.NotificationResponse;
import com.dobongsoon.BuyDobong.domain.consumer.notification.model.NotificationType;
import com.dobongsoon.BuyDobong.domain.consumer.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consumer/{consumerId}/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /** 공통 생성 */
    @PostMapping
    public ResponseEntity<NotificationResponse> create(
            @RequestParam Long consumerId,
            @RequestParam NotificationType type,
            @RequestParam String title,
            @RequestParam String body
    ) {
        return ResponseEntity.ok(
                notificationService.create(consumerId, type, title, body)
        );
    }

    /** 키워드 특가 알림 */
    @PostMapping("/keyword")
    public ResponseEntity<NotificationResponse> createKeywordDeal(
            @RequestParam Long consumerId,
            @RequestParam String keyword,
            @RequestParam String productName
    ) {
        return ResponseEntity.ok(
                notificationService.createKeywordDeal(consumerId, keyword, productName)
        );
    }

    /** 상점 특가 알림 */
    @PostMapping("/store")
    public ResponseEntity<NotificationResponse> createStoreDeal(
            @RequestParam Long consumerId,
            @RequestParam String storeName,
            @RequestParam String productName
    ) {
        return ResponseEntity.ok(
                notificationService.createStoreDeal(consumerId, storeName, productName)
        );
    }
}