package com.dobongsoon.BuyDobong.domain.notification.model;

import com.dobongsoon.BuyDobong.domain.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification",
        indexes = {
                @Index(name = "idx_notification_user", columnList = "user_id"),
                @Index(name = "idx_notification_created_at", columnList = "created_at")
        })
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    public static Notification of(User user, NotificationType type, String title, String body) {
        return Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .body(body)
                .build();
    }

    public static Notification keywordDeal(User user, String keyword, String productName) {
        return of(user,
                NotificationType.KEYWORD,
                "'" + keyword + "' 특가 소식 도착! 💸",
                "지금 '" + productName + "'이(가) 할인 가격으로 올라왔어요.\n오늘 메뉴 고민 끝!");
    }

    public static Notification storeDeal(User user, String storeName, String productName) {
        return of(user,
                NotificationType.STORE,
                "놓치면 아쉬운 " + storeName + " 특가! ⚡️",
                "오늘 등록된 " + productName + ", 금방 품절될 수 있어요.\n지금 확인해보세요.");
    }
}