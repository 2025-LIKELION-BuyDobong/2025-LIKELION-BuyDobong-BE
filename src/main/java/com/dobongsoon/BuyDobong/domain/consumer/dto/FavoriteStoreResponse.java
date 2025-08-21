package com.dobongsoon.BuyDobong.domain.consumer.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class FavoriteStoreResponse {
    private Long id;
    private String name;
    private String market;
    private boolean isOpen;
    private LocalDateTime createdAt;
}