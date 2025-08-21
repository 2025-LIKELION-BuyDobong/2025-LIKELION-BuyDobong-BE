package com.dobongsoon.BuyDobong.domain.consumer.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "keyword", uniqueConstraints = @UniqueConstraint(name="uk_keyword_word", columnNames = "word"))
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class Keyword {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String word;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}