package com.dobongsoon.BuyDobong.domain.sms.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class SmsCertificationConfirmRequest {
    private String phone;
    private String certificationNumber;
}
