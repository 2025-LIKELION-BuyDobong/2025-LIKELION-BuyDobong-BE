package com.dobongsoon.BuyDobong.domain.favorite.service;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.favorite.dto.FavoriteStoreResponse;
import com.dobongsoon.BuyDobong.domain.user.model.User;
import com.dobongsoon.BuyDobong.domain.favorite.model.FavoriteStore;
import com.dobongsoon.BuyDobong.domain.user.repository.UserRepository;
import com.dobongsoon.BuyDobong.domain.favorite.repository.FavoriteStoreRepository;
import com.dobongsoon.BuyDobong.domain.store.model.Store;
import com.dobongsoon.BuyDobong.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteStoreService {

    private final FavoriteStoreRepository favoriteStoreRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    // 관심 상점 등록
    @Transactional
    public FavoriteStoreResponse addFavorite(Long userId, Long storeId) {
        if (favoriteStoreRepository.existsByUser_IdAndStoreId(userId, storeId)) {
            throw new BusinessException(ErrorCode.FAVORITE_ALREADY_EXISTS);
        }

        // 존재 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        FavoriteStore saved = favoriteStoreRepository.save(
                FavoriteStore.builder().user(user).storeId(storeId).build()
        );

        return FavoriteStoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .market(store.getMarket().name())
                .imageUrl(store.getImageUrl())
                .isOpen(store.isOpen())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    // 관심 상점 목록 조회
    @Transactional(readOnly = true)
    public List<FavoriteStoreResponse> getFavorites(Long userId) {
        List<FavoriteStore> favorites =
                favoriteStoreRepository.findByUser_IdOrderByCreatedAtDesc(userId);

        if (favorites.isEmpty())    return List.of();

        List<Long> storeIds = favorites.stream()
                .map(FavoriteStore::getStoreId)
                .distinct()
                .toList();

        Map<Long, Store> storeMap = storeRepository
                .findAllById(favorites.stream().map(FavoriteStore::getStoreId).distinct().toList())
                .stream()
                .collect(Collectors.toMap(Store::getId, s -> s));

        if (storeMap.size() != storeIds.size()) {
            throw new BusinessException(ErrorCode.STORE_NOT_FOUND);
        }

        return favorites.stream().map(f -> {
            Store s = storeMap.get(f.getStoreId());
            return FavoriteStoreResponse.builder()
                    .id(s.getId())
                    .name(s.getName())
                    .market(s.getMarket().name())
                    .imageUrl(s.getImageUrl())
                    .isOpen(s.isOpen())
                    .createdAt(f.getCreatedAt())
                    .build();
        }).toList();
    }

    // 관심 상점 해제
    public void removeFavorite(Long userId, Long storeId) {
        boolean exists = favoriteStoreRepository.existsByUser_IdAndStoreId(userId, storeId);
        if (!exists) {
            throw new BusinessException(ErrorCode.FAVORITE_NOT_FOUND);
        }
        favoriteStoreRepository.deleteByUser_IdAndStoreId(userId, storeId);
    }
}