package com.dobongsoon.BuyDobong.domain.push.queue;

import com.dobongsoon.BuyDobong.domain.push.service.WebPushSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushJobWorker {

    private static final String STREAM_KEY = PushJobProducer.STREAM_KEY;
    private static final String GROUP = "push-workers";

    private final StringRedisTemplate redis;
    private final WebPushSender webPushSender;
    private final ObjectMapper om = new ObjectMapper();

    @Value("${spring.application.name:app}")
    private String appName;

    private String consumerName;

    @PostConstruct
    void init() {
        this.consumerName = appName + ":" + UUID.randomUUID();
        try {
            // 그룹 없으면 생성
            redis.opsForStream().createGroup(STREAM_KEY, ReadOffset.latest(), GROUP);
            log.info("Created consumer group '{}' on stream '{}'", GROUP, STREAM_KEY);
        } catch (Exception ignore) {
            // 이미 있으면 무시
            log.info("Consumer group '{}' already exists", GROUP);
        }
    }

    @Scheduled(fixedDelay = 500)
    public void poll() {
        // 블로킹으로 메시지 최대 20개 읽기 (기다렸다가 한 번 읽을 때 최대 20개의 job만 처리)
        List<MapRecord<String, Object, Object>> messages =
                redis.opsForStream().read(
                        Consumer.from(GROUP, consumerName),
                        StreamReadOptions.empty().block(Duration.ofMillis(200)).count(20),
                        StreamOffset.create(STREAM_KEY, ReadOffset.lastConsumed())
                );

        if (messages == null || messages.isEmpty()) return;

        for (MapRecord<String, Object, Object> msg : messages) {
            try {
                Map<Object, Object> val = msg.getValue();
                Long userId   = Long.valueOf(String.valueOf(val.getOrDefault("userId", "0")));
                String title  = String.valueOf(val.getOrDefault("title", ""));
                String body   = String.valueOf(val.getOrDefault("body", ""));
                String deeplink = String.valueOf(val.getOrDefault("deeplink", ""));

                // 실제 푸시 발송
                webPushSender.sendToUser(userId, title, body, deeplink);

                // ACK
                redis.opsForStream().acknowledge(STREAM_KEY, GROUP, msg.getId());
            } catch (Exception e) {
                log.error("Failed to process push message {}: {}", msg.getId(), e.getMessage(), e);
            }
        }
    }
}