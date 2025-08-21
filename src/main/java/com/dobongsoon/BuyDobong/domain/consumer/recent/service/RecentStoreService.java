package com.dobongsoon.BuyDobong.domain.consumer.recent.service;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.consumer.model.Consumer;
import com.dobongsoon.BuyDobong.domain.consumer.recent.model.RecentStore;
import com.dobongsoon.BuyDobong.domain.consumer.recent.repository.RecentStoreRepository;
import com.dobongsoon.BuyDobong.domain.consumer.repository.ConsumerRepository;
import com.dobongsoon.BuyDobong.domain.store.model.Store;
import com.dobongsoon.BuyDobong.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RecentStoreService {

    private final RecentStoreRepository recentStoreRepository;
    private final ConsumerRepository consumerRepository;
    private final StoreRepository storeRepository;

    // 최근 본 상점 기록 (존재하면 viewedAt 갱신)
    public RecentStore add(Long consumerId, Long storeId) {
        Consumer consumer = consumerRepository.findById(consumerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONSUMER_NOT_FOUND));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        return recentStoreRepository.findByConsumer_IdAndStore_Id(consumerId, storeId)
                .map(rs -> { rs.setViewedAt(now); return rs; })
                .orElseGet(() -> recentStoreRepository.save(
                        RecentStore.builder()
                                .consumer(consumer)
                                .store(store)
                                .viewedAt(now)
                                .build()
                ));
    }

    // 최근 본 상점 조회 (Top 5 최신순)
    @Transactional(readOnly = true)
    public List<RecentStore> list(Long consumerId) {
        return recentStoreRepository.findTop5ByConsumer_IdOrderByViewedAtDesc(consumerId);
    }

    // 최근 본 상점 삭제
    public void remove(Long consumerId, Long storeId) {
        RecentStore row = recentStoreRepository.findByConsumer_IdAndStore_Id(consumerId, storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RECENT_NOT_FOUND));
        recentStoreRepository.delete(row);
    }
}