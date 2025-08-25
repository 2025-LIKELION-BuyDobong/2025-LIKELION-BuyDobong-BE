package com.dobongsoon.BuyDobong.domain.consumer.repository;

import com.dobongsoon.BuyDobong.domain.consumer.model.Consumer;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConsumerRepository extends JpaRepository<Consumer, Long> {
    boolean existsByUser_Id(Long userId);
    Optional<Consumer> findByUser_Id(Long userId);

    // consumerId 리스트 → UserId 리스트로 변환 (알림에서 푸시 허용 여부 확인용)
    @Query("select c.user.id from Consumer c where c.id in :ids")
    List<Long> findUserIdsByConsumerIds(@Param("ids") List<Long> consumerIds);
}