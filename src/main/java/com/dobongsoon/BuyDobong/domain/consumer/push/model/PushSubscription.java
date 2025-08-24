package com.dobongsoon.BuyDobong.domain.consumer.push.model;

import com.dobongsoon.BuyDobong.domain.consumer.model.Consumer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "push_subscription",
        uniqueConstraints = @UniqueConstraint(name="uk_consumer_endpoint", columnNames = {"consumer_id","endpoint"}))
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushSubscription {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="consumer_id", nullable = false)
    private Consumer consumer;

    @Column(nullable = false, length = 1024)
    private String endpoint;

    @Column(nullable = false, length = 200)
    private String p256dh;

    @Column(nullable = false, length = 200)
    private String auth;

    @Column(nullable = false)
    private boolean active;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void activate(String p256dh, String auth) {
        this.p256dh = p256dh;
        this.auth   = auth;
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}