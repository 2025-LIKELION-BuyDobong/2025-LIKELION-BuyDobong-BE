package com.dobongsoon.BuyDobong.domain.recent.model;

import com.dobongsoon.BuyDobong.domain.user.model.User;
import com.dobongsoon.BuyDobong.domain.store.model.Store;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentStore {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="user_id", nullable=false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="store_id", nullable=false)
    private Store store;

    @Column(nullable=false)
    private LocalDateTime viewedAt;

    public void setViewedAt(LocalDateTime now) {
        this.viewedAt = now;
    }
}