package com.dobongsoon.BuyDobong.domain.push.model;

import com.dobongsoon.BuyDobong.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "push_subscription",
        uniqueConstraints = {
                // 같은 사용자가 동일 endpoint를 중복 등록하지 못하도록
                @UniqueConstraint(name = "uk_user_endpoint", columnNames = {"user_id", "endpoint"})
        },
        indexes = {
                @Index(name = "idx_push_sub_user", columnList = "user_id")
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushSubscription {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 2048)
    private String endpoint;

    @Column(nullable = false, length = 512)
    private String p256dh;

    @Column(nullable = false, length = 512)
    private String auth;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /* 편의 생성자 */
    public static PushSubscription of(User user, String endpoint, String p256dh, String auth) {
        return PushSubscription.builder()
                .user(user)
                .endpoint(endpoint)
                .p256dh(p256dh)
                .auth(auth)
                .build();
    }

    /* 내용 갱신(같은 endpoint 재구독 시 키만 바뀌는 케이스 대비) */
    public void updateKeys(String newP256dh, String newAuth) {
        this.p256dh = newP256dh;
        this.auth = newAuth;
    }
}