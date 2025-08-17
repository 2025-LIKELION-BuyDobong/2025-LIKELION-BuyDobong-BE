package com.dobongsoon.BuyDobong.domain.store.service;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreCreateRequest;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreResponse;
import com.dobongsoon.BuyDobong.domain.store.model.Store;
import com.dobongsoon.BuyDobong.domain.store.repository.StoreRepository;
import com.dobongsoon.BuyDobong.domain.user.model.User;
import com.dobongsoon.BuyDobong.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;


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
                .address(request.getAddress())
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
}
