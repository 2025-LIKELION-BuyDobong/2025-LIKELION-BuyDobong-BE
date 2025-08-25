package com.dobongsoon.BuyDobong.domain.consumer.push.controller;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.consumer.dto.PushPreferenceRequest;
import com.dobongsoon.BuyDobong.domain.consumer.dto.PushPreferenceResponse;
import com.dobongsoon.BuyDobong.domain.consumer.model.Consumer;
import com.dobongsoon.BuyDobong.domain.consumer.push.dto.PushSubscriptionRequest;
import com.dobongsoon.BuyDobong.domain.consumer.push.dto.PushSubscriptionResponse;
import com.dobongsoon.BuyDobong.domain.consumer.push.service.PushSubscriptionService;
import com.dobongsoon.BuyDobong.domain.consumer.repository.ConsumerRepository;
import com.dobongsoon.BuyDobong.domain.consumer.service.ConsumerPreferenceService;
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
@RequestMapping("/api/consumer")
@RequiredArgsConstructor
public class PushController {

    private final ConsumerPreferenceService preferenceService;
    private final PushSubscriptionService pushSubscriptionService;
    private final ConsumerRepository consumerRepository;

    private Long consumerIdOrThrow(Long userId) {
        return consumerRepository.findByUser_Id(userId)
                .map(Consumer::getId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONSUMER_NOT_FOUND));
    }

    @Operation(
            summary = "푸시 알림 설정 상태 조회",
            description = """
    현재 소비자의 푸시 알림 설정 상태를 조회합니다.
    - 인증 필요: CONSUMER
    - 응답: { "pushEnabled": true|false }
    - 레코드가 없으면 기본 false를 반환합니다.
    """
    )
    @GetMapping("/push")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<PushPreferenceResponse> getPush(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(
                preferenceService.getPushPreference(userId)
        );
    }

    @Operation(
            summary = "푸시 알림 on/off 설정",
            description = """
    소비자가 푸시 알림 수신 여부를 on/off 합니다.
    - 인증 필요: CONSUMER
    - 요청: { "pushEnabled": true|false }
    - 응답: { "pushEnabled": true|false }
    """
    )
    @PatchMapping("/push")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<PushPreferenceResponse> togglePush(
            @AuthenticationPrincipal Long userId,
            @RequestBody PushPreferenceRequest request
    ) {
        return ResponseEntity.ok(
                preferenceService.togglePush(userId, request.isPushEnabled())
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
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<PushSubscriptionResponse> subscribe(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PushSubscriptionRequest req
    ) {
        Long consumerId = consumerIdOrThrow(userId);
        return ResponseEntity.ok(pushSubscriptionService.subscribe(consumerId, req));
    }

    @Operation(
            summary = "PWA 구독 정보 삭제",
            description = """
    저장된 구독 정보를 삭제합니다.
    - 인증 필요: CONSUMER
    - 요청 파라미터: endpoint
    - 해당 endpoint가 존재하면 삭제합니다.
    - 응답: 204 No Content
    """
    )
    @DeleteMapping("/subscription")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<Void> unsubscribe(
            @AuthenticationPrincipal Long userId,
            @RequestParam String endpoint
    ) {
        Long consumerId = consumerIdOrThrow(userId);
        pushSubscriptionService.unsubscribe(consumerId, endpoint);
        return ResponseEntity.noContent().build();
    }
}