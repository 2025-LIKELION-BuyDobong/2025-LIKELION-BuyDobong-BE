package com.dobongsoon.BuyDobong.domain.push.queue;

public record PushJobPayload(
        Long userId,
        String title,
        String body,
        String deeplink
) {}