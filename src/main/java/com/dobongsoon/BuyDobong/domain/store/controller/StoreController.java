package com.dobongsoon.BuyDobong.domain.store.controller;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreCreateRequest;
import com.dobongsoon.BuyDobong.domain.store.dto.StoreResponse;
import com.dobongsoon.BuyDobong.domain.store.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

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

    @PostMapping("/me")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<StoreResponse> getMyStore(@AuthenticationPrincipal Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return ResponseEntity.ok(storeService.getMyStore(userId));
    }
}
