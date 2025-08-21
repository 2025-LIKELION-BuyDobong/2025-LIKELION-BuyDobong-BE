package com.dobongsoon.BuyDobong.domain.consumer.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "consumer_keyword",
        uniqueConstraints = @UniqueConstraint(name="uk_consumer_keyword",
                columnNames={"consumer_id", "keyword_id"}))
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConsumerKeyword {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consumer_id")
    private Consumer consumer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}