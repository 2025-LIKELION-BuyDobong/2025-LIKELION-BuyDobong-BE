package com.dobongsoon.BuyDobong.domain.store.repository;

import com.dobongsoon.BuyDobong.domain.store.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsByUser_Id(Long userId);
    Optional<Store> findByUser_Id(Long userId);

    @Query(value = "SELECT * FROM store ORDER BY RAND() LIMIT :size", nativeQuery = true)
    List<Store> findRandom(@org.springframework.data.repository.query.Param("size") int size);
}
