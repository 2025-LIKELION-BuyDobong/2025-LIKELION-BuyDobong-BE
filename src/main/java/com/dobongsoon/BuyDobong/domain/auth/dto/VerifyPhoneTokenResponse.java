package com.dobongsoon.BuyDobong.domain.auth.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class VerifyPhoneTokenResponse {
    private String verifiedPhoneToken;
}
