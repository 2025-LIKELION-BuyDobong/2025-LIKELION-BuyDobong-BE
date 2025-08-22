package com.dobongsoon.BuyDobong.domain.store.service;

import com.dobongsoon.BuyDobong.domain.store.dto.StoreCreateRequest;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreResponse;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreUpdateRequest;

public interface StoreService {
    StoreResponse register(Long userId, StoreCreateRequest request);
    StoreResponse update(Long userId, StoreUpdateRequest request);
    StoreResponse getMyStore(Long userId);
    StoreResponse openMyStore(Long userId, boolean open);
}
