package com.dobongsoon.BuyDobong.domain.consumer.model;

import com.dobongsoon.BuyDobong.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "consumer", uniqueConstraints = {
        @UniqueConstraint(name = "uk_consumer_user", columnNames = "user_id")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consumer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}