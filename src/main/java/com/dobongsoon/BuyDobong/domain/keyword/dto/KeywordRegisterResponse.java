package com.dobongsoon.BuyDobong.domain.keyword.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record KeywordRegisterResponse(
        Long id,
        Long consumerId,
        String word,
        LocalDateTime createdAt,
        boolean success
) {}