package com.dobongsoon.BuyDobong.domain.notification.repository;

import com.dobongsoon.BuyDobong.domain.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findTop30ByConsumer_IdOrderByCreatedAtDesc(Long consumerId);
}