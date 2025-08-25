package com.dobongsoon.BuyDobong.domain.consumer.service;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.consumer.dto.FavoriteStoreResponse;
import com.dobongsoon.BuyDobong.domain.consumer.model.Consumer;
import com.dobongsoon.BuyDobong.domain.consumer.model.FavoriteStore;
import com.dobongsoon.BuyDobong.domain.consumer.repository.ConsumerRepository;
import com.dobongsoon.BuyDobong.domain.consumer.repository.FavoriteStoreRepository;
import com.dobongsoon.BuyDobong.domain.store.model.Store;
import com.dobongsoon.BuyDobong.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteStoreService {

    private final FavoriteStoreRepository favoriteStoreRepository;
    private final ConsumerRepository consumerRepository;
    private final StoreRepository storeRepository;

    // 관심 상점 등록
    @Transactional
    public FavoriteStoreResponse addFavorite(Long consumerId, Long storeId) {
        if (favoriteStoreRepository.existsByConsumer_IdAndStoreId(consumerId, storeId)) {
            throw new BusinessException(ErrorCode.FAVORITE_ALREADY_EXISTS);
        }

        Consumer consumer = consumerRepository.findById(consumerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONSUMER_NOT_FOUND));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        FavoriteStore saved = favoriteStoreRepository.save(
                FavoriteStore.builder().consumer(consumer).storeId(storeId).build()
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
    public List<FavoriteStoreResponse> getFavorites(Long consumerId) {
        List<FavoriteStore> favorites =
                favoriteStoreRepository.findByConsumer_IdOrderByCreatedAtDesc(consumerId);

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
    public void removeFavorite(Long consumerId, Long storeId) {
        boolean exists = favoriteStoreRepository.existsByConsumer_IdAndStoreId(consumerId, storeId);
        if (!exists) {
            throw new BusinessException(ErrorCode.STORE_NOT_FOUND);
        }
        favoriteStoreRepository.deleteByConsumer_IdAndStoreId(consumerId, storeId);
    }
}