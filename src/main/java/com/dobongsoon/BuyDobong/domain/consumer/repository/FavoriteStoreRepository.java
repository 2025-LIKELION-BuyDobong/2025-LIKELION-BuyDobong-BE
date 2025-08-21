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

    // ✅ 명시 쿼리로 즐겨찾기 여부 체크 (네이밍 실수 방지)
    @Query("select count(f) > 0 from FavoriteStore f where f.consumer.id = :consumerId and f.storeId = :storeId")
    boolean isFavorite(@Param("consumerId") Long consumerId, @Param("storeId") Long storeId);

    // 관심 상점 해제
    void deleteByConsumer_IdAndStoreId(Long consumerId, Long storeId);
}