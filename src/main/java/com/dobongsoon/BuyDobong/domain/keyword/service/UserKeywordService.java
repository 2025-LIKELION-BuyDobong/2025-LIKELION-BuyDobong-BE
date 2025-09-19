package com.dobongsoon.BuyDobong.domain.keyword.service;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.keyword.dto.PopularKeywordDto;
import com.dobongsoon.BuyDobong.domain.keyword.model.UserKeyword;
import com.dobongsoon.BuyDobong.domain.keyword.model.Keyword;
import com.dobongsoon.BuyDobong.domain.keyword.repository.UserKeywordRepository;
import com.dobongsoon.BuyDobong.domain.user.model.User;
import com.dobongsoon.BuyDobong.domain.user.repository.UserRepository;
import com.dobongsoon.BuyDobong.domain.keyword.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserKeywordService {

    private static final int TOP_N = 10;

    private final UserRepository userRepository;
    private final KeywordRepository keywordRepository;
    private final UserKeywordRepository userKeywordRepository;

    // 관심 키워드 등록
    public UserKeyword add(Long userId, String word) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // keyword 사전에서 조회, 없으면 생성
        Keyword keyword = keywordRepository.findByWord(word)
                .orElseGet(() -> keywordRepository.save(
                        Keyword.builder().word(word).build()
                ));

        // 중복 등록 방지
        if (userKeywordRepository.existsByUser_IdAndKeyword_Id(userId, keyword.getId())) {
            throw new BusinessException(ErrorCode.KEYWORD_ALREADY_EXISTS);
        }

        // 관계 저장
        UserKeyword saved = UserKeyword.builder()
                .user(user)
                .keyword(keyword)
                .build();

        return userKeywordRepository.save(saved);
    }

    // 관심 키워드 목록 조회
    @Transactional(readOnly = true)
    public List<UserKeyword> list(Long userId) {
        return userKeywordRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }

    // 관심 키워드 삭제
    public void remove(Long userId, Long keywordId) {
        UserKeyword row = userKeywordRepository.findByUser_IdAndKeyword_Id(userId, keywordId)
                .orElseThrow(() -> new BusinessException(ErrorCode.KEYWORD_NOT_FOUND));

        userKeywordRepository.delete(row);
    }

    @Transactional(readOnly = true)
    public boolean isInterested(Long userId, String rawWord) {
        if (userId == null || rawWord == null || rawWord.isBlank()) return false;
        String word = rawWord.trim();
        return keywordRepository.findByWord(word)
                .map(k -> userKeywordRepository.existsByUser_IdAndKeyword_Id(userId, k.getId()))
                .orElse(false);
    }

    // 인기 키워드 top 10 조회
    public List<PopularKeywordDto> getTop10Keywords() {
        return userKeywordRepository.findTopKeywords(TOP_N).stream()
                .map(r -> new PopularKeywordDto(
                        (String) r[0],                 // word
                        ((Number) r[1]).longValue()    // cnt
                ))
                .toList();
    }
}