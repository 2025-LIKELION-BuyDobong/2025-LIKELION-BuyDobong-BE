package com.dobongsoon.BuyDobong.domain.consumer.service;

import com.dobongsoon.BuyDobong.domain.consumer.model.Consumer;
import com.dobongsoon.BuyDobong.domain.consumer.repository.ConsumerRepository;
import com.dobongsoon.BuyDobong.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsumerService {
    private final UserRepository userRepository;
    private final ConsumerRepository consumerRepository;

    public Long consumerIdOrNull(String phone) {
        if (phone == null) return null;

        return userRepository.findByPhone(phone)
                .flatMap(user -> consumerRepository.findByUser_Id(user.getId()))
                .map(Consumer::getId)
                .orElse(null);
    }

    public Long consumerIdOrNull(Long userId) {
        if (userId == null) return null;
        return consumerRepository.findByUser_Id(userId)
                .map(Consumer::getId)
                .orElse(null);
    }


}