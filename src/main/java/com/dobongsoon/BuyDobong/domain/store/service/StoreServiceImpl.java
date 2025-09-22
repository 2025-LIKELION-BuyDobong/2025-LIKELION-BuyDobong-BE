package com.dobongsoon.BuyDobong.domain.store.service;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.favorite.repository.FavoriteStoreRepository;
import com.dobongsoon.BuyDobong.domain.product.model.Product;
import com.dobongsoon.BuyDobong.domain.product.repository.ProductRepository;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreCreateRequest;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreResponse;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreUpdateRequest;
import com.dobongsoon.BuyDobong.domain.store.model.MarketName;
import com.dobongsoon.BuyDobong.domain.store.model.Store;
import com.dobongsoon.BuyDobong.domain.store.repository.StoreRepository;
import com.dobongsoon.BuyDobong.domain.user.model.User;
import com.dobongsoon.BuyDobong.domain.user.repository.UserCascadeDeleteRepository;
import com.dobongsoon.BuyDobong.domain.user.repository.UserRepository;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final UserCascadeDeleteRepository userCascadeDeleteRepository;

    @Override
    public StoreResponse register(Long userId, StoreCreateRequest request) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        if (storeRepository.existsByUser_Id(userId)) {
            throw new BusinessException(ErrorCode.STORE_ALREADY_EXISTS);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Store store = Store.builder()
                .user(user)
                .name(request.getName())
                .market(request.getMarket())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .imageUrl(request.getImageUrl())
                .build();

        try {
            return StoreResponse.from(storeRepository.save(store));
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.STORE_ALREADY_EXISTS);
        }
    }

    @Override
    public StoreResponse update(Long userId, StoreUpdateRequest request) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findByUser_Id(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        String updateName = (request.getName() != null) ? request.getName().trim() : store.getName();
        MarketName updateMarket = (request.getMarket() != null) ? request.getMarket() : store.getMarket();
        Double updateLatitue = (request.getLatitude() != null) ? request.getLatitude() : store.getLatitude();
        Double updateLongitude = (request.getLongitude() != null) ? request.getLongitude() : store.getLongitude();
        String updateImageUrl = (request.getImageUrl() != null) ? request.getImageUrl().trim() : store.getImageUrl();

        store.updateStore(updateName, updateMarket, updateLatitue, updateLongitude, updateImageUrl);

        return StoreResponse.from(store);
    }

    @Override
    public StoreResponse getMyStore(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Store store = storeRepository.findByUser_Id(userId).orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        return StoreResponse.from(store);
    }

    @Override
    public StoreResponse openMyStore(Long userId, boolean open) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Store store = storeRepository.findByUser_Id(userId).orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        store.changeOpen(open);

        return StoreResponse.from(store);
    }

    @Override
    public void deleteMyStoreImage(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Store store = storeRepository.findByUser_Id(userId).orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        store.updateStore(store.getName(), store.getMarket(), store.getLatitude(), store.getLongitude(), null);
    }

    @Override
    public void deleteMyStore(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Store store = storeRepository.findByUser_Id(userId).orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        Long storeId = store.getId();

        userCascadeDeleteRepository.deleteProductsByStoreId(storeId);
        userCascadeDeleteRepository.deleteRecentStoresByStoreId(storeId);
        userCascadeDeleteRepository.deleteFavoriteStoreByStoreId(storeId);

        userCascadeDeleteRepository.deleteStoreById(storeId);
    }
}
