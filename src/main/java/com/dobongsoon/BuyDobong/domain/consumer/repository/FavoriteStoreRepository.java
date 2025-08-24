package com.dobongsoon.BuyDobong.domain.consumer.repository;

import com.dobongsoon.BuyDobong.domain.consumer.model.FavoriteStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoriteStoreRepository extends JpaRepository<FavoriteStore, Long> {

    // 관심 상점 목록 조회
    List<FavoriteStore> findByConsumer_IdOrderByCreatedAtDesc(Long consumerId);

    // 특정 상점을 이미 관심 등록했는지 여부
    boolean existsByConsumer_IdAndStoreId(Long consumerId, Long storeId);

    // 관심 상점으로 등록한 소비자 ID 목록
    @Query("""
    select c.id
    from FavoriteStore f
    join f.consumer c
    left join ConsumerPreference p on p.userId = c.user.id
    where f.storeId = :storeId
      and (p is null or p.pushEnabled = true)
    """)
    List<Long> findPushEnabledConsumerIdsByStoreId(@Param("storeId") Long storeId);

    // 관심 상점 해제
    void deleteByConsumer_IdAndStoreId(Long consumerId, Long storeId);
}