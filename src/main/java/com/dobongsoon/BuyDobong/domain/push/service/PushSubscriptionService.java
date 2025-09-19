package com.dobongsoon.BuyDobong.domain.push.service;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.push.dto.PushSubscriptionRequest;
import com.dobongsoon.BuyDobong.domain.push.dto.PushSubscriptionResponse;
import com.dobongsoon.BuyDobong.domain.push.model.PushSubscription;
import com.dobongsoon.BuyDobong.domain.push.repository.PushSubscriptionRepository;
import com.dobongsoon.BuyDobong.domain.consumer.model.Consumer;
import com.dobongsoon.BuyDobong.domain.consumer.repository.ConsumerRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PushSubscriptionService {

    private final ConsumerRepository consumerRepository;
    private final PushSubscriptionRepository subscriptionRepository;

    /** 구독 등록/갱신 */
    public PushSubscriptionResponse subscribe(Long consumerId, PushSubscriptionRequest req) {
        Consumer consumer = consumerRepository.findById(consumerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONSUMER_NOT_FOUND));

        // upsert: 있으면 갱신, 없으면 생성
        PushSubscription sub = subscriptionRepository
                .findByConsumer_IdAndEndpoint(consumerId, req.getEndpoint())
                .orElseGet(() -> PushSubscription.of(
                        consumer,
                        req.getEndpoint(),
                        req.getP256dh(),
                        req.getAuth()
                ));

        // 키 값 갱신 (p256dh, auth 바뀌었을 경우)
        sub.updateKeys(req.getP256dh(), req.getAuth());

        return PushSubscriptionResponse.from(subscriptionRepository.save(sub));
    }

    /** 구독 해제 */
    public void unsubscribe(Long consumerId, String endpoint) {
        subscriptionRepository.findByConsumer_IdAndEndpoint(consumerId, endpoint)
                .ifPresent(subscriptionRepository::delete);
    }
}