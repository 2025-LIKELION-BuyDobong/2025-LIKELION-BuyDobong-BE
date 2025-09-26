package com.dobongsoon.BuyDobong.domain.push.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PushJobProducer {

    public static final String STREAM_KEY = "stream:push";
    private final StringRedisTemplate redis;
    private final ObjectMapper om = new ObjectMapper();

    public RecordId enqueue(PushJobPayload payload) {
        Map<String, String> map = Map.of(
                "userId", String.valueOf(payload.userId()),
                "title", payload.title(),
                "body", payload.body(),
                "deeplink", payload.deeplink() == null ? "" : payload.deeplink()
        );
        MapRecord<String, String, String> record =
                StreamRecords.mapBacked(map).withStreamKey(STREAM_KEY);
        return redis.opsForStream().add(record);
    }
}