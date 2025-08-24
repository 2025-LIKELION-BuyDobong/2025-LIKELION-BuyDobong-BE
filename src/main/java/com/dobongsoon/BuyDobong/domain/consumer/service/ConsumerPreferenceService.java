package com.dobongsoon.BuyDobong.domain.consumer.service;

import com.dobongsoon.BuyDobong.domain.consumer.dto.PushPreferenceResponse;
import com.dobongsoon.BuyDobong.domain.consumer.model.ConsumerPreference;
import com.dobongsoon.BuyDobong.domain.consumer.repository.ConsumerPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConsumerPreferenceService {
    private final ConsumerPreferenceRepository preferenceRepository;

    @Transactional
    public PushPreferenceResponse togglePush(Long userId, boolean enabled) {
        ConsumerPreference pref = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> preferenceRepository.save(
                        ConsumerPreference.builder()
                                .userId(userId)
                                .pushEnabled(true)
                                .build()
                ));

        pref.setPushEnabled(enabled);
        return PushPreferenceResponse.builder()
                .pushEnabled(pref.isPushEnabled())
                .build();
    }

    @Transactional(readOnly = true)
    public PushPreferenceResponse getPushPreference(Long userId) {
        boolean enabled = preferenceRepository.findByUserId(userId)
                .map(ConsumerPreference::isPushEnabled)
                .orElse(true);
        return PushPreferenceResponse.builder()
                .pushEnabled(enabled)
                .build();
    }
}