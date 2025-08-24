package com.dobongsoon.BuyDobong.domain.consumer.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "consumer_preference", uniqueConstraints = {
        @UniqueConstraint(name = "uk_consumer_pref_user", columnNames = "user_id")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumerPreference {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Setter
    @Column(name="push_enabled", nullable = false)
    private boolean pushEnabled;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static ConsumerPreference defaultOn(Long userId) {
        return ConsumerPreference.builder()
                .userId(userId)
                .pushEnabled(true)
                .build();
    }

}