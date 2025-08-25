package com.dobongsoon.BuyDobong.domain.consumer.notification.controller;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.consumer.model.Consumer;
import com.dobongsoon.BuyDobong.domain.consumer.notification.dto.NotificationResponse;
import com.dobongsoon.BuyDobong.domain.consumer.notification.service.NotificationService;
import com.dobongsoon.BuyDobong.domain.consumer.repository.ConsumerRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consumer/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final ConsumerRepository consumerRepository;

    private Long consumerIdOrThrow(Long userId) {
        return consumerRepository.findByUser_Id(userId)
                .map(Consumer::getId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONSUMER_NOT_FOUND));
    }

    @Operation(
            summary = "내 알림 목록 조회 (최신 30개)",
            description = """
    로그인한 소비자의 알림을 최신순으로 최대 30개 조회합니다.
    - 인증 필요: CONSUMER
    - 응답: [{ id, type, title, body, createdAt }]
    """
    )
    @GetMapping
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<List<NotificationResponse>> list(
            @AuthenticationPrincipal Long userId
    ) {
        Long consumerId = consumerRepository.findByUser_Id(userId)
                .map(Consumer::getId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONSUMER_NOT_FOUND));

        return ResponseEntity.ok(notificationService.listRecent30(consumerId));
    }

}