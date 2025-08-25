package com.dobongsoon.BuyDobong.common.config;

import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Security;

@Configuration
public class WebPushConfig {

    static {
        // ECDH/ECDSA 등 푸시 암호화에 필요
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Bean
    public PushService pushService(
            @Value("${push.vapid.public-key}") String publicKey,
            @Value("${push.vapid.private-key}") String privateKey,
            @Value("${push.vapid.subject}") String subject
    ) {
        try {
            PushService svc = new PushService();
            svc.setPublicKey(publicKey);    // VAPID Public
            svc.setPrivateKey(privateKey);  // VAPID Private
            svc.setSubject(subject);        // mailto: 또는 사이트 URL
            return svc;
        } catch (Exception e) {
            // setPublicKey / setPrivateKey가 알고리즘/키 스펙 예외를 던질 수 있음
            throw new IllegalStateException("Failed to initialize PushService (VAPID keys)", e);
        }
    }
}