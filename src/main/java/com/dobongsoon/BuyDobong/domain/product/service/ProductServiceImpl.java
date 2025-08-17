package com.dobongsoon.BuyDobong.domain.product.service;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductCreateRequest;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductDealRequest;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductResponse;
import com.dobongsoon.BuyDobong.domain.product.model.Product;
import com.dobongsoon.BuyDobong.domain.product.repository.ProductRepository;
import com.dobongsoon.BuyDobong.domain.store.model.Store;
import com.dobongsoon.BuyDobong.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    @Override
    public ProductResponse create(Long userId, ProductCreateRequest productCreateRequest) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Store store = storeRepository.findByUser_Id(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        if (productRepository.existsByStore_IdAndName(store.getId(), productCreateRequest.getName())) {
            throw new BusinessException(ErrorCode.PRODUCT_ALREADY_EXISTS);
        }

        Product product = Product.create(
                store,
                productCreateRequest.getName(),
                productCreateRequest.getRegularPrice(),
                productCreateRequest.getUnit(),
                productCreateRequest.getStockLevel()
        );

        Product savedProduct = productRepository.save(product);

        return ProductResponse.builder()
                .id(savedProduct.getId())
                .storeId(store.getId())
                .name(savedProduct.getName())
                .regularPrice(savedProduct.getRegularPrice())
                .unit(savedProduct.getUnit())
                .stockLevel(savedProduct.getStockLevel())
                .createdAt(savedProduct.getCreatedAt())
                .build();
    }

    @Override
    public ProductResponse deal(Long userId, Long productId, ProductDealRequest productDealRequest) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Product product = productRepository.findByIdAndStore_User_Id(productId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        LocalDateTime start = productDealRequest.getDealStartAt();
        LocalDateTime end = productDealRequest.getDealEndAt();

        if (productDealRequest.getDealPrice() == null || productDealRequest.getDealPrice() < 0) {
            throw new BusinessException(ErrorCode.INVALID_PRICE);
        }

        if(productDealRequest.getDealPrice() >= product.getRegularPrice()) {
            throw new BusinessException(ErrorCode.INVALID_PRICE);
        }

        if (start == null || end == null) {
            throw new BusinessException(ErrorCode.INVALID_PERIOD);
        }

        if (!start.isBefore(end)) {
            throw new BusinessException(ErrorCode.INVALID_PERIOD);
        }

        product.applyDeal(productDealRequest.getDealPrice(), productDealRequest.getDealStartAt(), productDealRequest.getDealEndAt());

        LocalDateTime now = LocalDateTime.now();

        productRepository.save(product);

        return ProductResponse.builder()
                .id(product.getId())
                .storeId(product.getStore().getId())
                .name(product.getName())
                .regularPrice(product.getRegularPrice())
                .unit(product.getUnit())
                .stockLevel(product.getStockLevel())
                .createdAt(product.getCreatedAt())
                .dealPrice(product.getDealPrice())
                .dealStartAt(product.getDealStartAt())
                .dealEndAt(product.getDealEndAt())
                .displayPrice(product.getDisplayPrice(now))
                .build();
    }

    @Override
    public List<ProductResponse> getMyProducts(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        storeRepository.findByUser_Id(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        return productRepository.findByStore_User_IdOrderByCreatedAtDesc(userId).stream()
                .map(p -> ProductResponse.builder()
                        .id(p.getId())
                        .storeId(p.getStore().getId())
                        .name(p.getName())
                        .regularPrice(p.getRegularPrice())
                        .unit(p.getUnit())
                        .stockLevel(p.getStockLevel())
                        .createdAt(p.getCreatedAt())
                        .dealPrice(p.getDealPrice())
                        .dealStartAt(p.getDealStartAt())
                        .dealEndAt(p.getDealEndAt())
                        .displayPrice(p.getDisplayPrice(now))
                        .build()
                ).toList();

    }
}
