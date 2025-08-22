package com.dobongsoon.BuyDobong.domain.auth.service;

import com.dobongsoon.BuyDobong.common.jwt.JwtProvider;
import com.dobongsoon.BuyDobong.domain.auth.dto.AuthResponse;
import com.dobongsoon.BuyDobong.domain.auth.dto.LoginRequest;
import com.dobongsoon.BuyDobong.domain.auth.dto.RegisterRequest;
import com.dobongsoon.BuyDobong.domain.consumer.model.Consumer;
import com.dobongsoon.BuyDobong.domain.consumer.model.ConsumerPreference;
import com.dobongsoon.BuyDobong.domain.consumer.repository.ConsumerPreferenceRepository;
import com.dobongsoon.BuyDobong.domain.consumer.repository.ConsumerRepository;
import com.dobongsoon.BuyDobong.domain.user.model.User;
import com.dobongsoon.BuyDobong.domain.user.model.UserRole;
import com.dobongsoon.BuyDobong.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final ConsumerRepository consumerRepository;
    private final ConsumerPreferenceRepository consumerPreferenceRepository;

    // 회원가입
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (!StringUtils.hasText(request.getVerifiedPhoneToken()))
            throw new IllegalArgumentException("전화번호 인증 토큰이 없습니다.");
        if (!StringUtils.hasText(request.getPassword()) ||
            !request.getPassword().equals(request.getPasswordConfirm()))
            throw new IllegalArgumentException("비밀번호/확인이 일치하지 않습니다.");

        var claims = jwtProvider.parse(request.getVerifiedPhoneToken()).getPayload();
        Boolean verified = claims.get("phone_verified", Boolean.class);
        String phone = claims.get("phone", String.class);

        if(!Boolean.TRUE.equals(verified) || !StringUtils.hasText(phone))
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");

        if(userRepository.existsByPhone(phone))
            throw new IllegalStateException("이미 가입된 전화번호입니다.");

        User user = User.builder()
                .phone(phone)
                .password(request.getPassword())
                .role(request.getRole())
                .build();
        userRepository.save(user);

        // role이 CONSUMER면 Consumer/Preference 자동 생성
        if (user.getRole() == UserRole.CONSUMER) {
            if (!consumerRepository.existsByUser_Id(user.getId())) {
                consumerRepository.save(
                        Consumer.builder().user(user).build()
                );
            }
            if (!consumerPreferenceRepository.existsByUserId(user.getId())) {
                consumerPreferenceRepository.save(
                        ConsumerPreference.defaultOn(user.getId()) // pushEnabled ON
                );
            }
        }

        return AuthResponse.builder()
                .accessToken(jwtProvider.createAccessToken(user))
                .role(user.getRole())
                .build();
    }

    // 로그인
    @Override
    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 전화번호입니다."));

        if(!user.getPassword().equals(request.getPassword()))
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");

        return AuthResponse.builder()
                .accessToken(jwtProvider.createAccessToken(user))
                .role(user.getRole())
                .build();
    }
}
