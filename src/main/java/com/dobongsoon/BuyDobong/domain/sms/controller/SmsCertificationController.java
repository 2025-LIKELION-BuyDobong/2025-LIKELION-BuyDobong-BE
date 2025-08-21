package com.dobongsoon.BuyDobong.domain.sms.controller;

import com.dobongsoon.BuyDobong.domain.auth.dto.VerifyPhoneTokenResponse;
import com.dobongsoon.BuyDobong.domain.sms.dto.SmsCertificationConfirmRequest;
import com.dobongsoon.BuyDobong.domain.sms.dto.SmsCertificationSendRequest;
import com.dobongsoon.BuyDobong.domain.sms.service.SmsCertificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sms")
public class SmsCertificationController {

    private final SmsCertificationService smsCertificationService;

    @PostMapping("/send")
    @Operation(summary = "회원가입 시 휴대폰 인증번호 발송")
    public ResponseEntity<Void> sendSms(@RequestBody SmsCertificationSendRequest request){
        smsCertificationService.sendSms(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/confirm")
    @Operation(summary = "회원가입 시 휴대폰 인증번호 확인")
    public ResponseEntity<VerifyPhoneTokenResponse> confirm(@RequestBody SmsCertificationConfirmRequest request){
        String token = smsCertificationService.verifySms(request);
        return ResponseEntity.ok(new VerifyPhoneTokenResponse(token));
    }
}
