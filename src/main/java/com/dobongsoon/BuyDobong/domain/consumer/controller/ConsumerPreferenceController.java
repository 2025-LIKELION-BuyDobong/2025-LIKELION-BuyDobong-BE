package com.dobongsoon.BuyDobong.domain.consumer.controller;

import com.dobongsoon.BuyDobong.domain.consumer.dto.PushPreferenceRequest;
import com.dobongsoon.BuyDobong.domain.consumer.dto.PushPreferenceResponse;
import com.dobongsoon.BuyDobong.domain.consumer.service.ConsumerPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consumer")
@RequiredArgsConstructor
public class ConsumerPreferenceController {

    private final ConsumerPreferenceService preferenceService;

    @Operation(
            summary = "푸시 알림 설정 조회",
            description = """
    현재 소비자의 푸시 알림 설정 상태를 조회합니다.
    - 인증 필요: CONSUMER
    - 응답 바디: { "pushEnabled": true|false }
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
    - 요청: { "enabled": true/false }
    - 응답: { "pushEnabled": true/false }
    """
    )
    @PatchMapping("/push")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<PushPreferenceResponse> togglePush(
            @AuthenticationPrincipal Long userId,
            @RequestBody PushPreferenceRequest request
    ) {
        return ResponseEntity.ok(
                preferenceService.togglePush(userId, request.isEnabled())
        );
    }
}