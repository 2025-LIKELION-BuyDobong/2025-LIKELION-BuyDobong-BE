package com.dobongsoon.BuyDobong.domain.sms.service;

import com.dobongsoon.BuyDobong.domain.sms.dto.SmsCertificationConfirmRequest;
import com.dobongsoon.BuyDobong.domain.sms.dto.SmsCertificationSendRequest;

public interface SmsCertificationService {
    void sendSms(SmsCertificationSendRequest request);
    String verifySms(SmsCertificationConfirmRequest request);
}
