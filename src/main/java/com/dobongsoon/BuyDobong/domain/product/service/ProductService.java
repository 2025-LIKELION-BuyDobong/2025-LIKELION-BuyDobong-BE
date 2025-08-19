package com.dobongsoon.BuyDobong.domain.product.service;

import com.dobongsoon.BuyDobong.domain.product.dto.ProductCreateRequest;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductDealRequest;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductResponse;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductUpdateRequest;

import java.util.List;

public interface ProductService {
    ProductResponse create(Long userId, ProductCreateRequest request);
    ProductResponse deal(Long userId, Long productId, ProductDealRequest request);
    ProductResponse hide(Long userId, Long productId, boolean hidden);
    ProductResponse update(Long userId, Long productId, ProductUpdateRequest request);
    ProductResponse endDeal(Long userId, Long productId);

    List<ProductResponse> getMyProducts(Long userId);

    void delete(Long userId, Long productId);
}
