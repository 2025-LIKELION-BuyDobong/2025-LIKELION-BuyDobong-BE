package com.dobongsoon.BuyDobong.domain.product.repository;

import com.dobongsoon.BuyDobong.domain.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByStore_IdAndName(Long storeId, String name);
    Optional<Product> findByIdAndStore_User_Id(Long storeId, Long userId);
}
