package com.dobongsoon.BuyDobong.domain.product.service;

import com.dobongsoon.BuyDobong.domain.product.dto.ProductCreateRequest;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductResponse;

public interface ProductService {
    ProductResponse create(Long userId, ProductCreateRequest request);
}
