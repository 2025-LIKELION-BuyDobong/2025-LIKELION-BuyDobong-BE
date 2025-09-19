package com.dobongsoon.BuyDobong.domain.notification.service;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.favorite.repository.FavoriteStoreRepository;
import com.dobongsoon.BuyDobong.domain.keyword.repository.ConsumerKeywordHit;
import com.dobongsoon.BuyDobong.domain.keyword.repository.ConsumerKeywordRepository;
import com.dobongsoon.BuyDobong.domain.notification.dto.NotificationResponse;
import com.dobongsoon.BuyDobong.domain.notification.model.Notification;
import com.dobongsoon.BuyDobong.domain.notification.repository.NotificationRepository;
import com.dobongsoon.BuyDobong.domain.push.service.WebPushSender;
import com.dobongsoon.BuyDobong.domain.user.repository.*;
import com.dobongsoon.BuyDobong.domain.product.model.Product;
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
    private final ConsumerKeywordRepository consumerKeywordRepository;
    private final FavoriteStoreRepository favoriteStoreRepository;
    private final StoreRepository storeRepository;

    private final WebPushSender webPushSender;

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

        // 웹 푸시도 DB 알림 내용 그대로 전송
        for (Notification n : notifications) {
            webPushSender.sendToUser(
                    n.getUser().getId(),
                    n.getTitle(),
                    n.getBody(),
                    "https://buy-dobong.vercel.app/marketDetil/" + storeId
            );
        }
    }

    /** 관심 키워드 특가 알림 (팬아웃) */
    public void fanoutKeywordDeal(Product product) {
        String productName = product.getName();

        // 관심 키워드가 productName에 매칭되고, push ON인 소비자 + 해당 키워드(word)까지 함께 조회
        List<ConsumerKeywordHit> hits =
                consumerKeywordRepository.findHitsForProductName(productName);

        if (hits.isEmpty()) return;

        List<Notification> notifications = hits.stream()
                .map(hit -> Notification.keywordDeal(
                        consumerRepository.getReferenceById(hit.getConsumerId()),
                        hit.getWord(),
                        productName
                ))
                .toList();

        notificationRepository.saveAll(notifications);

        // 웹 푸시도 DB 알림 내용 그대로 전송
        for (Notification n : notifications) {
            String keyword = n.getBody().contains("'")
                    ? n.getBody().split("'")[1] // 본문에서 키워드만 추출
                    : "";

            String deeplink = "https://buy-dobong.vercel.app/keywordSearch?query="
                    + java.net.URLEncoder.encode(keyword, java.nio.charset.StandardCharsets.UTF_8);

            webPushSender.sendToConsumer(
                    n.getConsumer().getId(),
                    n.getTitle(),
                    n.getBody(),
                    deeplink
            );
        }
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> listRecent30(Long consumerId) {
        return notificationRepository.findTop30ByConsumer_IdOrderByCreatedAtDesc(consumerId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }
}