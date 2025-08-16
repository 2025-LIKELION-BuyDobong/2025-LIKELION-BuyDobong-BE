package com.dobongsoon.BuyDobong.domain.sms.util;

import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmsCertificationUtil {

    private final DefaultMessageService messageService;

    @Value("${coolsms.senderNumber}")
    private String senderNumber;

    public SingleMessageSentResponse sendSms(String to, String verificationCode) {
        Message message = new Message();
        message.setFrom(senderNumber);
        message.setTo(to);
        message.setText("[BuyDobong] 인증번호는 " + verificationCode + "입니다.");

        SingleMessageSentResponse response =
                this.messageService.sendOne(new SingleMessageSendingRequest(message));

        return response;
    }
}
