package com.dobongsoon.BuyDobong.domain.auth.service;

import com.dobongsoon.BuyDobong.common.jwt.JwtProvider;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {

    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public void logout(String authorizationHeader){
        if(!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("유효한 Authorization 헤더가 필요합니다.");
        }

        final String token = authorizationHeader.substring(7).trim();
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("토큰이 비어 있습니다.");
        }

        final Date exp;
        try {
            exp = jwtProvider.parse(token).getPayload().getExpiration();
            if (exp == null)
                throw new JwtException("만료 시간이 지정되지 않은 토큰입니다.");
        } catch (Exception e) {
            throw new JwtException("유효하지 않은 토큰입니다.", e);
        }

        long ttlMillis = exp.getTime() - System.currentTimeMillis();
        if (ttlMillis > 0) {
            redisTemplate.opsForValue().set(token, "1", ttlMillis, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public boolean isBlacklisted(String token) {
        if (!StringUtils.hasText(token)) return false;
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }
}
