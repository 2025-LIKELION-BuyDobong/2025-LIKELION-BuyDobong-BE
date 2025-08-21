package com.dobongsoon.BuyDobong.domain.consumer.repository;

import com.dobongsoon.BuyDobong.domain.consumer.model.ConsumerPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsumerPreferenceRepository extends JpaRepository<ConsumerPreference, Long> {
    boolean existsByUserId(Long userId);
    Optional<ConsumerPreference> findByUserId(Long userId);
}