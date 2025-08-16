package com.dobongsoon.BuyDobong.domain.auth.dto;

import com.dobongsoon.BuyDobong.domain.user.model.UserRole;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String verifiedPhoneToken;
    private String password;
    private String passwordConfirm;
    private UserRole role;
}
