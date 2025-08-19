package com.dobongsoon.BuyDobong.domain.product.controller;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductCreateRequest;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductDealRequest;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductHideRequest;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductResponse;
import com.dobongsoon.BuyDobong.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<ProductResponse> createProduct(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ProductCreateRequest productCreateRequest
    ) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        ProductResponse response = productService.create(userId, productCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{productId}/deal")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<ProductResponse> dealProduct(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long productId,
            @Valid @RequestBody ProductDealRequest productDealRequest
    ) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        ProductResponse response = productService.deal(userId, productId, productDealRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{productId}/hide")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<ProductResponse> hideProduct(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long productId,
            @Valid @RequestBody ProductHideRequest productHideRequest
    ) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        ProductResponse response = productService.hide(userId, productId, productHideRequest.getHidden());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<List<ProductResponse>> listMyProducts(
            @AuthenticationPrincipal Long userId
    ) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return ResponseEntity.ok(productService.getMyProducts(userId));
    }
}
