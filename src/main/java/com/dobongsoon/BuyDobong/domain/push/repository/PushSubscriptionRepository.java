package com.dobongsoon.BuyDobong.domain.push.repository;

import com.dobongsoon.BuyDobong.domain.push.model.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
    List<PushSubscription> findByUser_Id(Long userId);

    List<PushSubscription> findByUser_IdIn(List<Long> userIds);

    Optional<PushSubscription> findByUser_IdAndEndpoint(Long userId, String endpoint);

    void deleteByUser_IdAndEndpoint(Long userId, String endpoint);
}