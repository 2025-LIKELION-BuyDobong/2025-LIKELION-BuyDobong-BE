package com.dobongsoon.BuyDobong.domain.keyword.repository;

import com.dobongsoon.BuyDobong.domain.keyword.model.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Optional<Keyword> findByWord(String word);
    boolean existsByWord(String word);
}