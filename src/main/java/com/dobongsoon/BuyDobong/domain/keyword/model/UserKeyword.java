package com.dobongsoon.BuyDobong.domain.keyword.model;

import com.dobongsoon.BuyDobong.domain.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_keyword",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_keyword", columnNames = {"user_id", "keyword_id"})
        },
        indexes = {
                @Index(name = "idx_user_keyword_user", columnList = "user_id"),
                @Index(name = "idx_user_keyword_keyword", columnList = "keyword_id")
        }
)
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserKeyword {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}