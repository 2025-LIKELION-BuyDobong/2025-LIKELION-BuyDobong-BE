package com.dobongsoon.BuyDobong.domain.keyword.service;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.consumer.model.Consumer;
import com.dobongsoon.BuyDobong.domain.keyword.model.ConsumerKeyword;
import com.dobongsoon.BuyDobong.domain.keyword.model.Keyword;
import com.dobongsoon.BuyDobong.domain.keyword.repository.ConsumerKeywordRepository;
import com.dobongsoon.BuyDobong.domain.consumer.repository.ConsumerRepository;
import com.dobongsoon.BuyDobong.domain.keyword.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsumerKeywordService {

    private final ConsumerRepository consumerRepository;
    private final KeywordRepository keywordRepository;
    private final ConsumerKeywordRepository consumerKeywordRepository;

    // 관심 키워드 등록
    public ConsumerKeyword add(Long consumerId, String word) {
        Consumer consumer = consumerRepository.findById(consumerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONSUMER_NOT_FOUND));

        // keyword 사전에서 조회, 없으면 생성
        Keyword keyword = keywordRepository.findByWord(word)
                .orElseGet(() -> keywordRepository.save(
                        Keyword.builder().word(word).build()
                ));

        // 중복 등록 방지
        if (consumerKeywordRepository.existsByConsumer_IdAndKeyword_Id(consumerId, keyword.getId())) {
            throw new BusinessException(ErrorCode.KEYWORD_ALREADY_EXISTS);
        }

        // 관계 저장
        ConsumerKeyword saved = ConsumerKeyword.builder()
                .consumer(consumer)
                .keyword(keyword)
                .build();

        return consumerKeywordRepository.save(saved);
    }

    // 관심 키워드 목록 조회
    @Transactional(readOnly = true)
    public List<ConsumerKeyword> list(Long consumerId) {
        return consumerKeywordRepository.findByConsumer_IdOrderByCreatedAtDesc(consumerId);
    }

    // 관심 키워드 삭제
    public void remove(Long consumerId, Long keywordId) {
        ConsumerKeyword row = consumerKeywordRepository.findByConsumer_IdAndKeyword_Id(consumerId, keywordId)
                .orElseThrow(() -> new BusinessException(ErrorCode.KEYWORD_NOT_FOUND));

        consumerKeywordRepository.delete(row);
    }

    @Transactional(readOnly = true)
    public boolean isInterested(Long consumerId, String rawWord) {
        if (consumerId == null || rawWord == null || rawWord.isBlank()) return false;
        String word = rawWord.trim();
        return keywordRepository.findByWord(word)
                .map(k -> consumerKeywordRepository.existsByConsumer_IdAndKeyword_Id(consumerId, k.getId()))
                .orElse(false);
    }
}