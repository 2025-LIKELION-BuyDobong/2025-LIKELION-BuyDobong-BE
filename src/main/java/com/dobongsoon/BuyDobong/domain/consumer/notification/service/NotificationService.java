package com.dobongsoon.BuyDobong.domain.consumer.notification.service;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.consumer.model.Consumer;
import com.dobongsoon.BuyDobong.domain.consumer.notification.dto.NotificationResponse;
import com.dobongsoon.BuyDobong.domain.consumer.notification.model.Notification;
import com.dobongsoon.BuyDobong.domain.consumer.notification.repository.NotificationRepository;
import com.dobongsoon.BuyDobong.domain.consumer.repository.ConsumerPreferenceRepository;
import com.dobongsoon.BuyDobong.domain.consumer.repository.ConsumerRepository;
import com.dobongsoon.BuyDobong.domain.consumer.repository.FavoriteStoreRepository;
import com.dobongsoon.BuyDobong.domain.store.model.Store;
import com.dobongsoon.BuyDobong.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ConsumerRepository consumerRepository;
    private final ConsumerPreferenceRepository consumerPreferenceRepository;
    private final FavoriteStoreRepository favoriteStoreRepository;
    private final StoreRepository storeRepository;

    /** 상점 특가 알림 (팬아웃) */
    public void fanoutStoreDeal(Long storeId, String productName) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        // push ON 된 소비자만 ID로 조회
        List<Long> consumerIds = favoriteStoreRepository.findPushEnabledConsumerIdsByStoreId(storeId);
        if (consumerIds.isEmpty()) return;

        // ID -> 프록시 -> Notification 엔티티로 매핑
        List<Notification> notifications = consumerIds.stream()
                .map(id -> Notification.storeDeal(consumerRepository.getReferenceById(id),
                        store.getName(), productName))
                .toList();

        notificationRepository.saveAll(notifications);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> listRecent30(Long consumerId) {
        return notificationRepository.findTop30ByConsumer_IdOrderByCreatedAtDesc(consumerId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }
}