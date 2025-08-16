package com.dobongsoon.BuyDobong.domain.sms.service;

import com.dobongsoon.BuyDobong.common.jwt.JwtProvider;
import com.dobongsoon.BuyDobong.domain.sms.dao.SmsCertificationDao;
import com.dobongsoon.BuyDobong.domain.sms.dto.SmsCertificationConfirmRequest;
import com.dobongsoon.BuyDobong.domain.sms.dto.SmsCertificationSendRequest;
import com.dobongsoon.BuyDobong.domain.sms.util.SmsCertificationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class SmsCertificationServiceImpl implements SmsCertificationService {

    private final SmsCertificationDao smsCertificationDao;
    private final SmsCertificationUtil smsCertificationUtil;
    private final JwtProvider jwtProvider;
    private final SecureRandom random = new SecureRandom();

    @Override
    public void sendSms(SmsCertificationSendRequest request){
        String to = request.getPhone();
        String code = String.format("%06d", random.nextInt(1_000_000));
        smsCertificationUtil.sendSms(to, code);
        smsCertificationDao.createSmsCertification(to, code);
    }

    @Override
    public String verifySms(SmsCertificationConfirmRequest request){
        String phone = request.getPhone();
        String code = request.getCertificationNumber();
        boolean verify =
                smsCertificationDao.hasKey(phone)
                && code != null
                && code.equals(smsCertificationDao.getSmsCertification(phone));

        if (!verify) throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        smsCertificationDao.removeSmsCertification(phone);
        return jwtProvider.createVerifiedPhoneToken(phone);
    }
}
