package com.dobongsoon.BuyDobong.domain.auth.service;

public interface LogoutService {
    void logout(String authorizationHeader);
    boolean isBlacklisted(String token);
}
