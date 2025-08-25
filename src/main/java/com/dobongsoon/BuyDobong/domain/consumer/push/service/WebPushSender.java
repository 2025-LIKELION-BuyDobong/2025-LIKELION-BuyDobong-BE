package com.dobongsoon.BuyDobong.domain.consumer.push.service;

import com.dobongsoon.BuyDobong.domain.consumer.push.model.PushSubscription;
import com.dobongsoon.BuyDobong.domain.consumer.push.repository.PushSubscriptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebPushSender {

    private final PushService pushService;
    private final PushSubscriptionRepository subscriptionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 단일 소비자 전송 → 결국 bulk 메서드 재사용 */
    public void sendToConsumer(Long consumerId, String title, String body, String url) {
        sendToConsumers(List.of(consumerId), title, body, url);
    }

    /** 여러 소비자에게 벌크 전송 */
    public void sendToConsumers(List<Long> consumerIds, String title, String body, String url) {
        if (consumerIds == null || consumerIds.isEmpty()) return;

        List<PushSubscription> subs = subscriptionRepository.findByConsumer_IdIn(consumerIds);
        if (subs.isEmpty()) {
            log.debug("No subscriptions for consumerIds={}", consumerIds);
            return;
        }
        byte[] payload = buildPayload(title, body, url);

        for (PushSubscription s : subs) {
            try {
                Notification n = new Notification(
                        s.getEndpoint(),
                        s.getP256dh(),
                        s.getAuth(),
                        payload
                );
                HttpResponse res = pushService.send(n);
                int status = res.getStatusLine().getStatusCode();

                if (status < 200 || status >= 300) {
                    log.warn("Bulk push failed: consumerId={}, status={}, endpoint={}",
                            s.getConsumer().getId(), status, s.getEndpoint());
                }
            } catch (Exception e) {
                log.warn("Bulk push error: consumerId={}, endpoint={}",
                        s.getConsumer().getId(), s.getEndpoint(), e);
            }
        }
    }

    /** SW가 파싱하기 쉬운 심플 JSON payload */
    private byte[] buildPayload(String title, String body, String url) {
        try {
            Map<String, Object> payload = Map.of(
                    "title", title,
                    "body", body,
                    "url", url
            );
            return objectMapper.writeValueAsString(payload).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build web push payload", e);
        }
    }
}