package com.dobongsoon.BuyDobong.domain.consumer.push.repository;

import com.dobongsoon.BuyDobong.domain.consumer.push.model.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
    Optional<PushSubscription> findByConsumer_IdAndEndpoint(Long consumerId, String endpoint);
    List<PushSubscription> findAllByConsumer_IdAndActiveTrue(Long consumerId);
    boolean existsByConsumer_IdAndEndpoint(Long consumerId, String endpoint);
    void deleteByConsumer_IdAndEndpoint(Long consumerId, String endpoint);
}