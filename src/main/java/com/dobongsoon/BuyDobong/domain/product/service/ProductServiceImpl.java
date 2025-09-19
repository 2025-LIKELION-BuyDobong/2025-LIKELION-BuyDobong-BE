package com.dobongsoon.BuyDobong.domain.product.service;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.notification.service.NotificationService;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductCreateRequest;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductDealRequest;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductDealUpdateRequest;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductResponse;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductUpdateRequest;
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

    private final NotificationService notificationService;

    private ProductResponse toResponse(Product p) {
        return toResponse(p, LocalDateTime.now());
    }

    private ProductResponse toResponse(Product p, LocalDateTime now) {
        return ProductResponse.builder()
                .id(p.getId())
                .storeId(p.getStore().getId())
                .name(p.getName())
                .regularPrice(p.getRegularPrice())
                .regularUnit(p.getRegularUnit())
                .stockLevel(p.getStockLevel())
                .createdAt(p.getCreatedAt())
                .dealPrice(p.getDealPrice())
                .dealUnit(p.getDealUnit())
                .dealStartAt(p.getDealStartAt())
                .dealEndAt(p.getDealEndAt())
                .displayPrice(p.getDisplayPrice(now))
                .displayUnit(p.getDisplayUnit(now))
                .hidden(p.isHidden())
                .build();
    }

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

        Product saved = productRepository.save(
                Product.create(store, productCreateRequest.getName(), productCreateRequest.getRegularPrice(), productCreateRequest.getRegularUnit(), productCreateRequest.getStockLevel())
        );

        return toResponse(saved);
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

        product.applyDeal(productDealRequest.getDealPrice(), productDealRequest.getDealUnit(), productDealRequest.getDealStartAt(), productDealRequest.getDealEndAt());

        // 관심 상점 특가 알림
        notificationService.fanoutStoreDeal(product.getStore().getId(), product.getName());
        // 관심 키워드 특가 알림
        notificationService.fanoutKeywordDeal(product);

        return toResponse(product);
    }

    @Override
    public ProductResponse update(Long userId, Long productId, ProductUpdateRequest productUpdateRequest) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Product product = productRepository.findByIdAndStore_User_Id(productId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        if (productUpdateRequest.getName() != null) {
            String updateName = productUpdateRequest.getName().trim();

            if (updateName.isEmpty()) {
                throw new BusinessException(ErrorCode.BAD_REQUEST);
            }
            if (updateName.equals(productUpdateRequest.getName())) {
                boolean check = productRepository.existsByStore_IdAndNameAndIdNot(product.getStore().getId(), updateName, product.getId());
                if (check) {
                    throw new BusinessException(ErrorCode.PRODUCT_ALREADY_EXISTS);
                }
                product.setName(updateName);
            }
        }

        if (productUpdateRequest.getRegularPrice() != null) {
            Long updateRegularPrice = productUpdateRequest.getRegularPrice().longValue();

            if (updateRegularPrice < 0) {
                throw new BusinessException(ErrorCode.INVALID_PRICE);
            }

            if (product.getDealPrice() != null && updateRegularPrice < product.getDealPrice()) {
                throw new BusinessException(ErrorCode.INVALID_PRICE);
            }

            product.setRegularPrice(updateRegularPrice);
        }

        if (productUpdateRequest.getRegularUnit() != null) {
            String updateRegularUnit = productUpdateRequest.getRegularUnit().trim();

            if (updateRegularUnit.isEmpty()) {
                throw new BusinessException(ErrorCode.BAD_REQUEST);
            }

            product.setRegularUnit(updateRegularUnit);
        }

        if (productUpdateRequest.getStockLevel() != null) {
            product.setStockLevel(productUpdateRequest.getStockLevel());
        }

        return toResponse(product);
    }

    @Override
    public ProductResponse hide(Long userId, Long productId, boolean hidden) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Product product = productRepository.findByIdAndStore_User_Id(productId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        product.changeHidden(hidden);

        return toResponse(product);
    }

    @Override
    public ProductResponse updateDeal(Long userId, Long productId, ProductDealUpdateRequest productDealUpdateRequest) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Product product = productRepository.findByIdAndStore_User_Id(productId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        Long updatePrice = (productDealUpdateRequest.getDealPrice() != null) ? productDealUpdateRequest.getDealPrice().longValue() : product.getDealPrice();
        String updateUnit = (productDealUpdateRequest.getDealUnit() != null) ? productDealUpdateRequest.getDealUnit().trim() : product.getDealUnit();

        LocalDateTime dealStart = (productDealUpdateRequest.getDealStartAt() != null) ? productDealUpdateRequest.getDealStartAt() : product.getDealStartAt();
        LocalDateTime dealEnd = (productDealUpdateRequest.getDealEndAt() != null) ? productDealUpdateRequest.getDealEndAt() : product.getDealEndAt();

        if (updatePrice < 0) throw new BusinessException(ErrorCode.INVALID_PRICE);
        if (updatePrice > product.getRegularPrice()) throw new BusinessException(ErrorCode.INVALID_PRICE);
        if (!dealStart.isBefore(dealEnd)) throw new BusinessException(ErrorCode.BAD_REQUEST);

        product.applyDeal(updatePrice, updateUnit, dealStart, dealEnd);

        return toResponse(product);
    }

    @Override
    public ProductResponse endDeal(Long userId, Long productId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Product product = productRepository.findByIdAndStore_User_Id(productId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        product.endDealNow(now);

        return toResponse(product, now);
    }

    @Override
    public List<ProductResponse> getMyProducts(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        storeRepository.findByUser_Id(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        return productRepository.findByStore_User_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(p -> toResponse(p, now))
                .toList();
    }

    @Override
    public void delete(Long userId, Long productId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Product product = productRepository.findByIdAndStore_User_Id(productId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        productRepository.delete(product);
    };
}
