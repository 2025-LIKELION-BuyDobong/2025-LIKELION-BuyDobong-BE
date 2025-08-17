package com.dobongsoon.BuyDobong.domain.store.repository;

import com.dobongsoon.BuyDobong.domain.store.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsByUser_Id(Long userId);
    Optional<Store> findByUser_Id(Long userId);
}
