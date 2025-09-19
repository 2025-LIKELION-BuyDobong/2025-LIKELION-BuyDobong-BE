package com.dobongsoon.BuyDobong.domain.keyword.repository;

import com.dobongsoon.BuyDobong.domain.keyword.dto.PopularKeywordDto;
import com.dobongsoon.BuyDobong.domain.keyword.model.UserKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {
    boolean existsByUser_IdAndKeyword_Id(Long userId, Long keywordId);

    List<UserKeyword> findByUser_IdOrderByCreatedAtDesc(Long userId);

    Optional<UserKeyword> findByUser_IdAndKeyword_Id(Long userId, Long keywordId);

    @Query(value = """
        SELECT k.word AS word, COUNT(uk.id) AS cnt
        FROM user_keyword uk
        JOIN keyword k ON k.id = uk.keyword_id
        GROUP BY k.id, k.word
        ORDER BY cnt DESC
        LIMIT :n
    """, nativeQuery = true)
    List<Object[]> findTopKeywords(@Param("n") int n);

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