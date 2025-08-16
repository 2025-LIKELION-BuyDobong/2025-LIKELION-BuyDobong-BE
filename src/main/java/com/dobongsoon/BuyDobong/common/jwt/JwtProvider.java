package com.dobongsoon.BuyDobong.common.jwt;

import com.dobongsoon.BuyDobong.domain.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTtlMs;     // Acccess Token 만료 시간
    private final long phoneTtlMs;      // 전화번호 인증 Token 만료 시간

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-ttl-ms}") long accessTtlMs,
            @Value("${jwt.phone-ttl-ms}") long phoneTtlMs
    ){
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTtlMs = accessTtlMs;
        this.phoneTtlMs = phoneTtlMs;
    }

    // 로그인 Access Token 발급
    public String createAccessToken(User user){
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("phone", user.getPhone())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(accessTtlMs)))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    // 전화번호 인증 완료 토큰
    public String createVerifiedPhoneToken(String phone){
        Instant now = Instant.now();
        return Jwts.builder()
                .subject("phone-verified")
                .claims(Map.of("phone", phone, "phone_verified", true))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(phoneTtlMs)))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
    }
}
