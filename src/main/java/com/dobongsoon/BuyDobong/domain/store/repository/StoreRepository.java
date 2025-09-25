package com.dobongsoon.BuyDobong.domain.store.repository;

import com.dobongsoon.BuyDobong.domain.store.model.MarketName;
import com.dobongsoon.BuyDobong.domain.store.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsByUser_Id(Long userId);
    Optional<Store> findByUser_Id(Long userId);

    // 상점 이름으로 검색
    @Query("""
        select s
          from Store s
         where lower(s.name) like lower(concat('%', :q, '%'))
           and (:markets is null or s.market in :markets)
    """)
    List<Store> searchByName(@Param("q") String keyword,
                             @Param("markets") List<MarketName> markets);

    // 상점 랜덤 둘러보기
    @Query(value = "SELECT * FROM store ORDER BY RAND() LIMIT :size", nativeQuery = true)
    List<Store> findRandom(@Param("size") int size);
}
