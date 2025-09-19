package com.dobongsoon.BuyDobong.domain.push.service;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.push.dto.PushSubscriptionRequest;
import com.dobongsoon.BuyDobong.domain.push.dto.PushSubscriptionResponse;
import com.dobongsoon.BuyDobong.domain.push.model.PushSubscription;
import com.dobongsoon.BuyDobong.domain.push.repository.PushSubscriptionRepository;
import com.dobongsoon.BuyDobong.domain.user.model.User;
import com.dobongsoon.BuyDobong.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PushSubscriptionService {

    private final UserRepository userRepository;
    private final PushSubscriptionRepository subscriptionRepository;

    /** 구독 등록/갱신 */
    public PushSubscriptionResponse subscribe(Long userId, PushSubscriptionRequest req) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 2) 구독 정보 업서트
        PushSubscription existing = subscriptionRepository
                .findByUser_IdAndEndpoint(userId, req.getEndpoint())
                .orElse(null);

        if (existing != null) {
            // 같은 endpoint 재구독: 키만 갱신
            existing.updateKeys(req.getP256dh(), req.getAuth());

            return PushSubscriptionResponse.from(subscriptionRepository.save(existing));
        }

        // 3) 신규 생성 (User는 프록시 참조로 쿼리 최소화)
        User userRef = userRepository.getReferenceById(userId);
        PushSubscription created = PushSubscription.builder()
                .user(userRef)
                .endpoint(req.getEndpoint())
                .p256dh(req.getP256dh())
                .auth(req.getAuth())
                .build();

        return PushSubscriptionResponse.from(subscriptionRepository.save(created));
    }

    /** 구독 해제 */
    public void unsubscribe(Long userId, String endpoint) {
        subscriptionRepository.deleteByUser_IdAndEndpoint(userId, endpoint);
    }
}