package com.dobongsoon.BuyDobong.domain.keyword.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record KeywordResponse(
        Long id,
        String word,
        LocalDateTime createdAt
) {}