package com.dobongsoon.BuyDobong.domain.consumer.repository;

import com.dobongsoon.BuyDobong.domain.consumer.model.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsumerRepository extends JpaRepository<Consumer, Long> {
    boolean existsByUser_Id(Long userId);
    Optional<Consumer> findByUser_Id(Long userId);
}