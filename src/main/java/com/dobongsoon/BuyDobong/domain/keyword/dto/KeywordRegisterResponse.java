package com.dobongsoon.BuyDobong.domain.keyword.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record KeywordRegisterResponse(
        Long id,
        Long userId,
        String word,
        LocalDateTime createdAt,
        boolean success
) {}