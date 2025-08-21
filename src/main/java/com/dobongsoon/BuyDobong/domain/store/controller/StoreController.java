package com.dobongsoon.BuyDobong.domain.store.controller;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.consumer.model.Consumer;
import com.dobongsoon.BuyDobong.domain.consumer.repository.ConsumerRepository;
import com.dobongsoon.BuyDobong.domain.consumer.service.ConsumerService;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreCreateRequest;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreDetailDto;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreOpenRequest;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreResponse;
import com.dobongsoon.BuyDobong.domain.store.service.StoreQueryService;
import com.dobongsoon.BuyDobong.domain.store.service.StoreService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final StoreQueryService storeQueryService;
    private final ConsumerService consumerService;
    private final ConsumerRepository consumerRepository;

    @PostMapping
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<StoreResponse> createStore(
            @AuthenticationPrincipal Long userId,
            Authentication authentication,
            @Valid @RequestBody StoreCreateRequest storeCreateRequest
    ) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(storeService.register(userId, storeCreateRequest));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<StoreResponse> getMyStore(@AuthenticationPrincipal Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return ResponseEntity.ok(storeService.getMyStore(userId));
    }

    @PostMapping("/open")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<StoreResponse> openStore(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody StoreOpenRequest storeOpenRequest
    ) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return ResponseEntity.ok(storeService.openMyStore(userId, storeOpenRequest.getOpen()));
    }

    @GetMapping("/{storeId}/detail/{consumerId}")
    public ResponseEntity<StoreDetailDto> getStoreDetail(
            @PathVariable Long storeId,
            @PathVariable(required = false) Long consumerId
    ) {
        return ResponseEntity.ok(storeQueryService.getStoreDetail(storeId, consumerId));
    }
}
