package com.dobongsoon.BuyDobong.domain.product.controller;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductCreateRequest;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductDealRequest;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductDealUpdateRequest;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductHideRequest;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductResponse;
import com.dobongsoon.BuyDobong.domain.product.dto.ProductUpdateRequest;
import com.dobongsoon.BuyDobong.domain.product.service.ProductService;
import com.dobongsoon.BuyDobong.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('MERCHANT')")
    @Operation(summary = "상품 등록")
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
    @Operation(summary = "특가 등록")
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


    @PatchMapping("/{productId}/deal")
    @PreAuthorize("hasRole('MERCHANT')")
    @Operation(summary = "특가 수정")
    public ResponseEntity<ProductResponse> updateDeal(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long productId,
            @Valid @RequestBody ProductDealUpdateRequest productDealUpdateRequestroductDealUpdateRequest
    ) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return ResponseEntity.ok(productService.updateDeal(userId, productId, productDealUpdateRequestroductDealUpdateRequest));
    }

    @PatchMapping("/{productId}/deal/end")
    @PreAuthorize("hasRole('MERCHANT')")
    @Operation(summary = "특가 즉시 종료")
    public ResponseEntity<ProductResponse> endDeal(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long productId
    ) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return ResponseEntity.ok(productService.endDeal(userId, productId));
    }

    @PostMapping("/{productId}/hide")
    @PreAuthorize("hasRole('MERCHANT')")
    @Operation(summary = "상품 숨기기")
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

    @PatchMapping("/{productId}")
    @PreAuthorize("hasRole('MERCHANT')")
    @Operation(summary = "상품 수정")
    public ResponseEntity<ProductResponse> updateProduct(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequest productUpdateRequest
    ) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        ProductResponse response = productService.update(userId, productId, productUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('MERCHANT')")
    @Operation(summary = "내 상품 조회")
    public ResponseEntity<List<ProductResponse>> listMyProducts(
            @AuthenticationPrincipal Long userId
    ) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return ResponseEntity.ok(productService.getMyProducts(userId));
    }

    @DeleteMapping("/{productid}")
    @PreAuthorize("hasRole('MERCHANT')")
    @Operation(summary = "상품 삭제")
    public ResponseEntity<ProductResponse> deleteProduct(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long productid
    ) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        productService.delete(userId, productid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
