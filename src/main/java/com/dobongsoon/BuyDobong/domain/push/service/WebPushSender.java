package com.dobongsoon.BuyDobong.domain.push.service;

import com.dobongsoon.BuyDobong.domain.push.model.PushSubscription;
import com.dobongsoon.BuyDobong.domain.push.repository.PushSubscriptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebPushSender {

    private final @Qualifier("webPushClient") nl.martijndwars.webpush.PushService pushClient;
    private final PushSubscriptionRepository subscriptionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 단일 사용자 전송 → 결국 bulk 메서드 재사용 */
    public void sendToUser(Long userId, String title, String body, String url) {
        sendToUsers(List.of(userId), title, body, url);
    }

    /** 여러 사용자에게 벌크 전송 */
    public void sendToUsers(List<Long> userIds, String title, String body, String url) {
        if (userIds == null || userIds.isEmpty()) return;

        List<PushSubscription> subs = subscriptionRepository.findByUser_IdIn(userIds);
        if (subs.isEmpty()) {
            log.debug("No subscriptions for userIds={}", userIds);
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
                HttpResponse res = pushClient.send(n);
                int status = res.getStatusLine().getStatusCode();

                if (status == 404 || status == 410) {
                    // 죽은 구독 정리
                    subscriptionRepository.delete(s);
                    log.info("Deleted dead subscription: userId={}, endpoint={}", s.getUser().getId(), s.getEndpoint());
                } else if (status < 200 || status >= 300) {
                    log.warn("Bulk push failed: userId={}, status={}, endpoint={}", s.getUser().getId(), status, s.getEndpoint());
                }
            } catch (Exception e) {
                log.warn("Bulk push error: userId={}, endpoint={}",
                        s.getUser().getId(), s.getEndpoint(), e);
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