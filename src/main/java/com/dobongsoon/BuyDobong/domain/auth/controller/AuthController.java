package com.dobongsoon.BuyDobong.domain.auth.controller;

import com.dobongsoon.BuyDobong.domain.auth.dto.AuthResponse;
import com.dobongsoon.BuyDobong.domain.auth.dto.LoginRequest;
import com.dobongsoon.BuyDobong.domain.auth.dto.RegisterRequest;
import com.dobongsoon.BuyDobong.domain.auth.service.AuthService;
import com.dobongsoon.BuyDobong.domain.auth.service.LogoutService;
import com.dobongsoon.BuyDobong.domain.auth.service.WithdrawService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final LogoutService logoutService;
    private final WithdrawService withdrawService;

    @PostMapping("/register")
    @Operation(summary = "회원가입")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    public ResponseEntity<Void> logout(
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader) {
        logoutService.logout(authorizationHeader);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/withdraw")
    @Operation(summary = "회원 탈퇴")
    public ResponseEntity<Void> withdraw(
            @RequestHeader(name = "Authorization", required = false) String authroizationHeader) {
        withdrawService.withdraw(authroizationHeader);
        return ResponseEntity.noContent().build();
    }
}
