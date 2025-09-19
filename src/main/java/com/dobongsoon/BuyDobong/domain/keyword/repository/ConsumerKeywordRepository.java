package com.dobongsoon.BuyDobong.domain.keyword.repository;

import com.dobongsoon.BuyDobong.domain.keyword.model.ConsumerKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConsumerKeywordRepository extends JpaRepository<ConsumerKeyword, Long> {
    boolean existsByConsumer_IdAndKeyword_Id(Long consumerId, Long keywordId);
    List<ConsumerKeyword> findByConsumer_IdOrderByCreatedAtDesc(Long consumerId);
    Optional<ConsumerKeyword> findByConsumer_IdAndKeyword_Id(Long consumerId, Long keywordId);

    @Query("""
        select ck.consumer.id as consumerId, ck.keyword.word as word
        from ConsumerKeyword ck
        join ck.keyword k
        where :productName like concat('%', k.word, '%')
          and exists (
              select 1 from ConsumerPreference p
              where p.userId = ck.consumer.user.id
              and p.pushEnabled = true
          )
    """)
    List<ConsumerKeywordHit> findHitsForProductName(String productName);
}