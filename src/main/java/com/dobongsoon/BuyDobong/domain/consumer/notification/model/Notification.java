package com.dobongsoon.BuyDobong.domain.consumer.notification.model;

import com.dobongsoon.BuyDobong.domain.consumer.model.Consumer;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumer_id", nullable = false)
    private Consumer consumer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationType type;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob @Column(nullable = false)
    private String body;

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name="created_at")
    private LocalDateTime createdAt;
}