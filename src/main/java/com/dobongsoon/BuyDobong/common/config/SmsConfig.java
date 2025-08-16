package com.dobongsoon.BuyDobong.common.config;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsConfig {

    @Bean
    public DefaultMessageService defaultMessageService(
            @Value("${coolsms.apiKey}") String apiKey,
            @Value("${coolsms.apiSecret}") String apiSecret,
            @Value("${coolsms.endpoint}") String endpoint
    ) {
        return NurigoApp.INSTANCE.initialize(apiKey, apiSecret, endpoint);
    }
}
