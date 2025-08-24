package com.dobongsoon.BuyDobong.domain.consumer.notification.controller;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.consumer.model.Consumer;
import com.dobongsoon.BuyDobong.domain.consumer.notification.dto.NotificationRequest;
import com.dobongsoon.BuyDobong.domain.consumer.notification.dto.NotificationResponse;
import com.dobongsoon.BuyDobong.domain.consumer.notification.model.NotificationType;
import com.dobongsoon.BuyDobong.domain.consumer.notification.service.NotificationService;
import com.dobongsoon.BuyDobong.domain.consumer.repository.ConsumerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consumer/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final ConsumerRepository consumerRepository;

    private Long consumerIdOrThrow(Long userId) {
        return consumerRepository.findByUserId(userId)
                .map(Consumer::getId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONSUMER_NOT_FOUND));
    }

    @PostMapping("/keyword")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<NotificationResponse> createKeywordDeal(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid NotificationRequest request
    ) {
        Long consumerId = consumerIdOrThrow(userId);
        return ResponseEntity.ok(
                notificationService.createKeywordDeal(
                        consumerId,
                        request.getKeyword(),
                        request.getProductName()
                )
        );
    }

    @PostMapping("/store")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<NotificationResponse> createStoreDeal(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid NotificationRequest request
    ) {
        Long consumerId = consumerIdOrThrow(userId);
        return ResponseEntity.ok(
                notificationService.createStoreDeal(
                        consumerId,
                        request.getStoreName(),
                        request.getProductName()
                )
        );
    }
}