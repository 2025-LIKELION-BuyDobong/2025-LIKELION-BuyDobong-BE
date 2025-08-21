package com.dobongsoon.BuyDobong.domain.consumer.repository;

import com.dobongsoon.BuyDobong.domain.consumer.model.ConsumerKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsumerKeywordRepository extends JpaRepository<ConsumerKeyword, Long> {
    boolean existsByConsumer_IdAndKeyword_Id(Long consumerId, Long keywordId);
    List<ConsumerKeyword> findByConsumer_IdOrderByCreatedAtDesc(Long consumerId);
    Optional<ConsumerKeyword> findByConsumer_IdAndKeyword_Id(Long consumerId, Long keywordId);
    void deleteByConsumer_IdAndKeyword_Id(Long consumerId, Long keywordId);
}