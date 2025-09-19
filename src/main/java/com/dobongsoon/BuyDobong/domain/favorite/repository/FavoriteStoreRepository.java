package com.dobongsoon.BuyDobong.domain.favorite.repository;

import com.dobongsoon.BuyDobong.domain.favorite.model.FavoriteStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoriteStoreRepository extends JpaRepository<FavoriteStore, Long> {

    // 관심 상점 목록 조회
    List<FavoriteStore> findByUser_IdOrderByCreatedAtDesc(Long userId);

    // 특정 상점을 이미 관심 등록했는지 여부
    boolean existsByUser_IdAndStoreId(Long userId, Long storeId);

    // 관심 상점으로 등록한 소비자 ID 목록
    @Query("""
        select u.id
        from FavoriteStore f
        join f.user u
        where f.storeId = :storeId
          and u.pushEnabled = true
    """)
    List<Long> findPushEnabledUserIdsByStoreId(@Param("storeId") Long storeId);

    // 관심 상점 해제
    void deleteByUser_IdAndStoreId(Long userId, Long storeId);
}