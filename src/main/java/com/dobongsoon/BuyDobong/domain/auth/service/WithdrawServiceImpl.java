package com.dobongsoon.BuyDobong.domain.auth.service;

import com.dobongsoon.BuyDobong.common.jwt.JwtProvider;
import com.dobongsoon.BuyDobong.domain.user.model.User;
import com.dobongsoon.BuyDobong.domain.user.repository.UserCascadeDeleteRepository;
import com.dobongsoon.BuyDobong.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WithdrawServiceImpl implements WithdrawService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final UserCascadeDeleteRepository userCascadeDeleteRepository;
    private final LogoutService logoutService;

    @Override
    @Transactional
    public void withdraw(String authorizationHeader) {
        if(!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("유효한 Authorization 헤더가 필요합니다.");
        }

        final String token = authorizationHeader.substring(7).trim();
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("토큰이 비어 있습니다.");
        }

        final Claims claims;
        try {
            claims = jwtProvider.parse(token).getPayload();
        } catch (Exception e) {
            throw new JwtException("유호하지 않은 토큰입니다.", e);
        }

        final String sub = claims.getSubject();
        if (!StringUtils.hasText(sub)) {
            throw new IllegalArgumentException("토큰에 userId가 없습니다.");
        }

        final Long userId;
        try {
            userId = Long.parseLong(sub);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("토큰 subject가 userId 형식이 아닙니다.", e);
        }

        if (!userRepository.existsById(userId)) {
            logoutService.logout(authorizationHeader);
            return;
        }

        // 참조 삭제
        Long storeId = userCascadeDeleteRepository.findStoreIdByUserId(userId);

        if (storeId != null) {
            userCascadeDeleteRepository.deleteProductsByStoreId(storeId);
            userCascadeDeleteRepository.deleteRecentStoresByStoreId(storeId);
        }

        userCascadeDeleteRepository.deletePushSubscriptionsByUserId(userId);
        userCascadeDeleteRepository.deleteNotificationsByUserId(userId);
        userCascadeDeleteRepository.deleteUserKeywordsByUserId(userId);
        userCascadeDeleteRepository.deleteFavoriteStoresByUserId(userId);
        userCascadeDeleteRepository.deleteRecentStoresByUserId(userId);

        if (storeId != null) {
            userCascadeDeleteRepository.deleteStoreById(storeId);
        }

        userCascadeDeleteRepository.deleteUserById(userId);
        
        logoutService.logout(authorizationHeader);
    }
}
