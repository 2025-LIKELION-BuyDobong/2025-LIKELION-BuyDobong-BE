package com.dobongsoon.BuyDobong.domain.consumer.push.service;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.consumer.push.dto.PushSubscriptionRequest;
import com.dobongsoon.BuyDobong.domain.consumer.push.dto.PushSubscriptionResponse;
import com.dobongsoon.BuyDobong.domain.consumer.push.model.PushSubscription;
import com.dobongsoon.BuyDobong.domain.consumer.push.repository.PushSubscriptionRepository;
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

    public PushSubscriptionResponse subscribe(Long consumerId, PushSubscriptionRequest req) {
        Consumer consumer = consumerRepository.findById(consumerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONSUMER_NOT_FOUND));

        // upsert: 있으면 갱신+active, 없으면 생성
        PushSubscription sub = subscriptionRepository
                .findByConsumer_IdAndEndpoint(consumerId, req.getEndpoint())
                .orElseGet(() -> PushSubscription.builder()
                        .consumer(consumer)
                        .endpoint(req.getEndpoint())
                        .p256dh(req.getP256dh())
                        .auth(req.getAuth())
                        .active(true)
                        .build());

        sub.activate(req.getP256dh(), req.getAuth());
        return PushSubscriptionResponse.from(subscriptionRepository.save(sub));
    }

    public void unsubscribe(Long consumerId, String endpoint) {
        subscriptionRepository.findByConsumer_IdAndEndpoint(consumerId, endpoint)
                .ifPresent(sub -> {
                    sub.deactivate(); // 소프트 해제
                    // 필요 시 하드 삭제로 바꾸려면 아래 사용
                    // subscriptionRepository.delete(sub);
                });
    }
}