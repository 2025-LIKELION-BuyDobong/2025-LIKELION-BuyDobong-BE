package com.dobongsoon.BuyDobong.domain.auth.dto;

import com.dobongsoon.BuyDobong.domain.user.model.UserRole;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private UserRole role;          // ex) MERCHANT
}
