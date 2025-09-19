package com.dobongsoon.BuyDobong.domain.push.service;

import com.dobongsoon.BuyDobong.domain.user.model.User;
import com.dobongsoon.BuyDobong.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PushService {
    private final UserRepository userRepo;

    @Transactional
    public boolean setPushEnabled(Long userId, boolean enabled) {
        var user = userRepo.findById(userId).orElseThrow();
        user.setPushEnabled(enabled);
        return user.isPushEnabled();
    }

    @Transactional(readOnly = true)
    public boolean getPushEnabled(Long userId) {
        return userRepo.findById(userId).map(User::isPushEnabled).orElse(true);
    }
}