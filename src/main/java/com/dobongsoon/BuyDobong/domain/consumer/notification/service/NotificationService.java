package com.dobongsoon.BuyDobong.domain.consumer.notification.service;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.consumer.model.Consumer;
import com.dobongsoon.BuyDobong.domain.consumer.notification.dto.NotificationResponse;
import com.dobongsoon.BuyDobong.domain.consumer.notification.model.Notification;
import com.dobongsoon.BuyDobong.domain.consumer.notification.model.NotificationType;
import com.dobongsoon.BuyDobong.domain.consumer.notification.repository.NotificationRepository;
import com.dobongsoon.BuyDobong.domain.consumer.repository.ConsumerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ConsumerRepository consumerRepository;

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .body(n.getBody())
                .createdAt(n.getCreatedAt())
                .build();
    }

    // 공통 기록
    public NotificationResponse create(Long consumerId, NotificationType type, String title, String body) {
        Consumer consumer = consumerRepository.findById(consumerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONSUMER_NOT_FOUND));

        Notification saved = notificationRepository.save(
                Notification.builder()
                        .consumer(consumer)
                        .type(type)
                        .title(title)
                        .body(body)
                        .build()
        );

        return toResponse(saved);
    }

    /** 편의: 관심 키워드 특가 알림 */
    public NotificationResponse createKeywordDeal(Long consumerId, String keyword, String productName) {
        String title = "‘" + keyword + "’ 특가 소식 도착! 💸";
        String body  = "지금 ‘" + productName + "’이(가) 할인 가격으로 올라왔어요.\n오늘 메뉴 고민 끝!";
        return create(consumerId, NotificationType.KEYWORD, title, body);
    }

    /** 편의: 관심 상점 특가 알림 */
    public NotificationResponse createStoreDeal(Long consumerId, String storeName, String productName) {
        String title = "놓치면 아쉬운 " + storeName + " 특가! ⚡️";
        String body  = "오늘 등록된 " + productName + ", 금방 품절될 수 있어요.\n지금 확인해보세요.";
        return create(consumerId, NotificationType.STORE, title, body);
    }
}