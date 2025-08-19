package com.dobongsoon.BuyDobong.domain.product.repository;

import com.dobongsoon.BuyDobong.domain.product.model.Product;
import org.springframework.data.jpa.repository.*;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // 생성 시 사용하는 중복 조회
    boolean existsByStore_IdAndName(Long storeId, String name);
    Optional<Product> findByIdAndStore_User_Id(Long storeId, Long userId);
    List<Product> findByStore_User_IdOrderByCreatedAtDesc(Long userId);
    // 수정 시 사용하는 중복 조회 (본인 제외)
    boolean existsByStore_IdAndNameAndIdNot(Long storeId, String name, Long id);

    // 상점 상세 조회 시 사용하는 전체 조회 (숨김 제외)
    List<Product> findByStore_IdAndHiddenFalse(Long storeId);
}
