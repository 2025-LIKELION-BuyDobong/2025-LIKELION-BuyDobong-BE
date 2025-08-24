package com.dobongsoon.BuyDobong.domain.consumer.repository;

import com.dobongsoon.BuyDobong.domain.consumer.model.ConsumerPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ConsumerPreferenceRepository extends JpaRepository<ConsumerPreference, Long> {
    boolean existsByUserId(Long userId);
    Optional<ConsumerPreference> findByUserId(Long userId);
    List<ConsumerPreference> findByUserIdIn(Collection<Long> userIds);

    @Query("""
    select c.id
    from ConsumerPreference p
    join Consumer c on c.user.id = p.userId
    where p.pushEnabled = true and c.id in :consumerIds
    """)
    List<Long> findPushEnabledConsumerIds(Collection<Long> consumerIds);
}