package com.dobongsoon.BuyDobong.domain.recent.repository;

import com.dobongsoon.BuyDobong.domain.recent.model.RecentStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecentStoreRepository extends JpaRepository<RecentStore, Long> {

    Optional<RecentStore> findByUser_IdAndStore_Id(Long userId, Long storeId);

    List<RecentStore> findTop5ByUser_IdOrderByViewedAtDesc(Long userId);

    long deleteByUser_IdAndStore_Id(Long userId, Long storeId);
}