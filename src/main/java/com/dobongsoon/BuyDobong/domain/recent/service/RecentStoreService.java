package com.dobongsoon.BuyDobong.domain.recent.service;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.user.model.User;
import com.dobongsoon.BuyDobong.domain.recent.model.RecentStore;
import com.dobongsoon.BuyDobong.domain.recent.repository.RecentStoreRepository;
import com.dobongsoon.BuyDobong.domain.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    /** 최근 본 상점 기록 (있으면 viewedAt 갱신) */
    public RecentStore add(Long userId, Long storeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        return recentStoreRepository.findByUser_IdAndStore_Id(userId, storeId)
                .map(rs -> { rs.setViewedAt(now); return rs; })
                .orElseGet(() -> recentStoreRepository.save(
                        RecentStore.builder()
                                .user(user)
                                .store(store)
                                .viewedAt(now)
                                .build()
                ));
    }

    /** 최근 본 상점 조회 (Top 5 최신순) */
    @Transactional(readOnly = true)
    public List<RecentStore> list(Long userId) {
        return recentStoreRepository.findTop5ByUser_IdOrderByViewedAtDesc(userId);
    }

    /** 최근 본 상점 삭제 */
    public void remove(Long userId, Long storeId) {
        long deleted = recentStoreRepository.deleteByUser_IdAndStore_Id(userId, storeId);
        if (deleted == 0) {
            throw new BusinessException(ErrorCode.RECENT_NOT_FOUND);
        }
    }
}