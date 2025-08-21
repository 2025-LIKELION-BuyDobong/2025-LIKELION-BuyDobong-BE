package com.dobongsoon.BuyDobong.domain.consumer.recent.repository;

import com.dobongsoon.BuyDobong.domain.consumer.recent.model.RecentStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecentStoreRepository extends JpaRepository<RecentStore, Long> {

    Optional<RecentStore> findByConsumer_IdAndStore_Id(Long consumerId, Long storeId);

    List<RecentStore> findTop5ByConsumer_IdOrderByViewedAtDesc(Long consumerId);
}