package com.dobongsoon.BuyDobong.domain.store.repository;

import com.dobongsoon.BuyDobong.domain.store.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsByUser_Id(Long userId);
}
