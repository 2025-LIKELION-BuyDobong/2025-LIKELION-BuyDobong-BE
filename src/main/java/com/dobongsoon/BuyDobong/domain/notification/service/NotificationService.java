package com.dobongsoon.BuyDobong.domain.notification.service;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.favorite.repository.FavoriteStoreRepository;
import com.dobongsoon.BuyDobong.domain.keyword.repository.UserKeywordRepository;
import com.dobongsoon.BuyDobong.domain.notification.dto.NotificationResponse;
import com.dobongsoon.BuyDobong.domain.notification.model.Notification;
import com.dobongsoon.BuyDobong.domain.notification.repository.NotificationRepository;
import com.dobongsoon.BuyDobong.domain.push.service.WebPushSender;
import com.dobongsoon.BuyDobong.domain.product.model.Product;
import com.dobongsoon.BuyDobong.domain.store.model.Store;
import com.dobongsoon.BuyDobong.domain.store.repository.StoreRepository;
import com.dobongsoon.BuyDobong.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final UserKeywordRepository userKeywordRepository;
    private final FavoriteStoreRepository favoriteStoreRepository;
    private final StoreRepository storeRepository;

    private final WebPushSender webPushSender;

    /** 상점 특가 알림 (팬아웃) */
    public void fanoutStoreDeal(Long storeId, String productName) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        // push ON 된 소비자만 ID로 조회
        List<Long> userIds = favoriteStoreRepository.findPushEnabledUserIdsByStoreId(storeId);
        if (userIds.isEmpty()) return;

        // 알림 엔티티 저장 (User 프록시 사용)
        List<Notification> notifications = userIds.stream()
                .map(id -> Notification.storeDeal(userRepository.getReferenceById(id),
                        store.getName(), productName))
                .toList();

        notificationRepository.saveAll(notifications);

        // 웹 푸시가 DB 알림 내용 그대로 전송
        String deeplink = "https://buy-dobong.vercel.app/marketDetil/" + storeId;
        for (Notification n : notifications) {
            webPushSender.sendToUser(
                    n.getUser().getId(),
                    n.getTitle(),
                    n.getBody(),
                    deeplink
            );
        }
    }

    /** 관심 키워드 특가 알림 (팬아웃) */
    public void fanoutKeywordDeal(Product product) {
        String productName = product.getName();

        // 관심 키워드가 productName에 매칭되고, push ON인 사용자 + 해당 키워드(word)까지 함께 조회
        List<UserKeywordRepository.UserKeywordHit> hits =
                userKeywordRepository.findHitsForProductName(productName);
        if (hits.isEmpty()) return;

        // 알림 저장
        List<Notification> notifications = hits.stream()
                .map(hit -> Notification.keywordDeal(
                        userRepository.getReferenceById(hit.getUserId()),
                        hit.getWord(),
                        productName
                ))
                .toList();

        notificationRepository.saveAll(notifications);

        // 웹 푸시가 DB 알림 내용 그대로 전송
        for (Notification n : notifications) {
            String keyword = n.getBody().contains("'")
                    ? n.getBody().split("'")[1] // 본문에서 키워드만 추출
                    : "";

            String deeplink = "https://buy-dobong.vercel.app/keywordSearch?query="
                    + java.net.URLEncoder.encode(keyword, java.nio.charset.StandardCharsets.UTF_8);

            webPushSender.sendToUser(
                    n.getUser().getId(),
                    n.getTitle(),
                    n.getBody(),
                    deeplink
            );
        }
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> listRecent30(Long userId) {
        return notificationRepository.findTop30ByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }
}