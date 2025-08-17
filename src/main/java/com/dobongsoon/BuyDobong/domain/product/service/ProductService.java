package com.dobongsoon.BuyDobong.domain.product.service;

import com.dobongsoon.BuyDobong.domain.product.dto.ProductCreateRequest;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductDealRequest;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductResponse;

public interface ProductService {
    ProductResponse create(Long userId, ProductCreateRequest request);
    ProductResponse deal(Long userId, Long productId, ProductDealRequest request);
}
