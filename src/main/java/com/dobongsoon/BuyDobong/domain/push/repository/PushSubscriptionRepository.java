package com.dobongsoon.BuyDobong.domain.push.repository;

import com.dobongsoon.BuyDobong.domain.push.model.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
    List<PushSubscription> findByConsumer_Id(Long consumerId);

    List<PushSubscription> findByConsumer_IdIn(List<Long> consumerIds);

    Optional<PushSubscription> findByConsumer_IdAndEndpoint(Long consumerId, String endpoint);

    void deleteByConsumer_IdAndEndpoint(Long consumerId, String endpoint);
}