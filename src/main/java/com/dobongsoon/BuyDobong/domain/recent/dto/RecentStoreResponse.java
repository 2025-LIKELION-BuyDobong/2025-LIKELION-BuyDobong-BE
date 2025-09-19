package com.dobongsoon.BuyDobong.domain.recent.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RecentStoreResponse {
    private Long id;
    private String name;
    private String market;
    private String imageUrl;
    private boolean open;
    private LocalDateTime viewedAt;
}