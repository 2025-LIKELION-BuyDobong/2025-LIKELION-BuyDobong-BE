package com.dobongsoon.BuyDobong.domain.sms.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class SmsCertificationDao {

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${otp.key-prefix}")
    private String otpKeyPrefix;

    @Value("${otp.ttl-seconds}")
    private int otpTtlSeconds;

    private String key(String phone){
        return otpKeyPrefix + phone;
    }

    // OTP 코드 + TTL 저장
    public void createSmsCertification(String phone, String code){
        stringRedisTemplate
                .opsForValue()
                .set(key(phone), code, Duration.ofSeconds(otpTtlSeconds));
    }

    // OTP 코드 조회
    public String getSmsCertification(String phone){
        return stringRedisTemplate.opsForValue().get(key(phone));
    }

    // OTP 코드 삭제
    public void removeSmsCertification(String phone){
        stringRedisTemplate.delete(key(phone));
    }

    // OTP 코드 존재 여부 확인
    public boolean hasKey(String phone){
        return stringRedisTemplate.hasKey(key(phone));
    }
}
