package com.dobongsoon.BuyDobong.domain.notification.controller;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.notification.dto.NotificationResponse;
import com.dobongsoon.BuyDobong.domain.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "내 알림 목록 조회 (최신 30개)",
            description = """
    로그인한 사용자의 알림을 최신순으로 최대 30개 조회합니다.
    - 인증 필요: CONSUMER
    - 응답: [{ id, type, title, body, createdAt }]
    """
    )
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationResponse>> list(
            @AuthenticationPrincipal Long userId
    ) {
        if (userId == null) throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        return ResponseEntity.ok(notificationService.listRecent30(userId));
    }

}