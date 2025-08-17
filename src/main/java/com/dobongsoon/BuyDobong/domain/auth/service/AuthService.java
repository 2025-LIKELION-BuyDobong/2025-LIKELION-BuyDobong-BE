package com.dobongsoon.BuyDobong.domain.auth.service;

import com.dobongsoon.BuyDobong.domain.auth.dto.AuthResponse;
import com.dobongsoon.BuyDobong.domain.auth.dto.LoginRequest;
import com.dobongsoon.BuyDobong.domain.auth.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}