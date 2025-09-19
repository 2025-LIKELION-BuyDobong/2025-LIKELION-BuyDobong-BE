package com.dobongsoon.BuyDobong.domain.keyword.repository;

import com.dobongsoon.BuyDobong.domain.keyword.model.UserKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {
    boolean existsByUser_IdAndKeyword_Id(Long userId, Long keywordId);

    List<UserKeyword> findByUser_IdOrderByCreatedAtDesc(Long userId);

    Optional<UserKeyword> findByUser_IdAndKeyword_Id(Long userId, Long keywordId);

    // pushEnabled = true 사용자만
    @Query("""
        select uk.user.id as userId, k.word as word
        from UserKeyword uk
        join uk.keyword k
        where :productName like concat('%', k.word, '%')
          and uk.user.pushEnabled = true
    """)
    List<UserKeywordHit> findHitsForProductName(String productName);

    interface UserKeywordHit {
        Long getUserId();
        String getWord();
    }
}