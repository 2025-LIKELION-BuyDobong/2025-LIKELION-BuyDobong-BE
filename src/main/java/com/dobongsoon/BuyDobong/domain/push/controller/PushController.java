package com.dobongsoon.BuyDobong.domain.push.controller;

import com.dobongsoon.BuyDobong.domain.push.dto.PushPreferenceRequest;
import com.dobongsoon.BuyDobong.domain.push.dto.PushPreferenceResponse;
import com.dobongsoon.BuyDobong.domain.push.dto.PushSubscriptionRequest;
import com.dobongsoon.BuyDobong.domain.push.dto.PushSubscriptionResponse;
import com.dobongsoon.BuyDobong.domain.push.service.PushService;
import com.dobongsoon.BuyDobong.domain.push.service.PushSubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PushController {

    private final PushService pushService;
    private final PushSubscriptionService pushSubscriptionService;

    @Operation(
            summary = "푸시 알림 설정 상태 조회",
            description = """
    현재 사용자의 푸시 알림 설정 상태를 조회합니다.
    - 인증 필요
    - 응답: { "pushEnabled": true|false }
    - 레코드가 없으면 기본 false를 반환합니다.
    """
    )
    @GetMapping("/push")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PushPreferenceResponse> getPush(
            @AuthenticationPrincipal Long userId
    ) {
        boolean enabled = pushService.getPushEnabled(userId);
        return ResponseEntity.ok(
                PushPreferenceResponse.builder().pushEnabled(enabled).build()
        );
    }

    @Operation(
            summary = "푸시 알림 on/off 설정",
            description = """
    사용자가 푸시 알림 수신 여부를 on/off 합니다.
    - 인증 필요
    - 요청: { "pushEnabled": true|false }
    - 응답: { "pushEnabled": true|false }
    """
    )
    @PatchMapping("/push")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PushPreferenceResponse> togglePush(
            @AuthenticationPrincipal Long userId,
            @RequestBody PushPreferenceRequest request
    ) {
        boolean enabled = pushService.setPushEnabled(userId, request.isPushEnabled());
        return ResponseEntity.ok(
                PushPreferenceResponse.builder().pushEnabled(enabled).build()
        );
    }

    @Value("${push.vapid.public-key}")
    private String vapidPublicKey;

    @Operation(
            summary = "VAPID 공개키 조회",
            description = """
        프론트에서 브라우저 구독 시 필요한 VAPID 공개키를 제공합니다.
        - 인증 불필요
        - 응답: { "publicKey": "<VAPID_PUBLIC_KEY>" }
        """
    )
    @GetMapping("/vapid")
    public Map<String, String> getPublicKey() {
        return Map.of("publicKey", vapidPublicKey);
    }

    @Operation(
            summary = "PWA 구독 정보 저장/갱신",
            description = """
    브라우저 PWA에서 발급받은 구독 정보를 서버에 등록합니다.
    - 인증 필요: CONSUMER
    - 요청: { "endpoint", "p256dh", "auth" }
    - 같은 endpoint가 이미 있으면 키 값만 갱신 처리됩니다.
    - 응답: { id, endpoint, createdAt }
    """
    )
    @PostMapping("/subscription")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PushSubscriptionResponse> subscribe(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PushSubscriptionRequest req
    ) {
        return ResponseEntity.ok(pushSubscriptionService.subscribe(userId, req));
    }

    @Operation(
            summary = "PWA 구독 정보 삭제",
            description = """
    저장된 구독 정보를 삭제합니다.
    - 인증 필요
    - 요청 파라미터: endpoint
    - 해당 endpoint가 존재하면 삭제합니다.
    - 응답: 204 No Content
    """
    )
    @DeleteMapping("/subscription")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unsubscribe(
            @AuthenticationPrincipal Long userId,
            @RequestParam String endpoint
    ) {
        pushSubscriptionService.unsubscribe(userId, endpoint);
        return ResponseEntity.noContent().build();
    }
}