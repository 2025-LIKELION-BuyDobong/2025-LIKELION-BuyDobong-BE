package com.dobongsoon.BuyDobong.domain.product.model;

import com.dobongsoon.BuyDobong.domain.store.model.Store;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "product",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_store_name",
                        columnNames = {"store_id", "name"}
                )
        },
        indexes = {
                @Index(name = "idx_product_store", columnList = "store_id")
        }
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long regularPrice;

    @Column(nullable = false)
    private String regularUnit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockLevel stockLevel;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean hidden = false;

    // 특가

    private Long dealPrice;
    private String dealUnit;
    private LocalDateTime dealStartAt;
    private LocalDateTime dealEndAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean dealActive = false;

    public static Product create(Store store, String name, Long regularPrice, String regularUnit, StockLevel stockLevel) {
        return Product.builder()
                .store(store)
                .name(name)
                .regularPrice(regularPrice)
                .regularUnit(regularUnit)
                .stockLevel(stockLevel)
                .build();
    }

    public void applyDeal(Long price, String unit, LocalDateTime startAt, LocalDateTime endAt) {
        this.dealPrice = price;
        this.dealUnit = unit;
        this.dealStartAt = startAt;
        this.dealEndAt = endAt;
        this.dealActive = isDealActive(LocalDateTime.now());
    }

    public void clearDeal() {
        this.dealPrice = null;
        this.dealUnit = null;
        this.dealStartAt = null;
        this.dealEndAt = null;
        this.dealActive = false;
    }

    public boolean isDealActive(LocalDateTime now) {
        if (dealPrice == null || dealStartAt == null || dealEndAt == null) return false;
        boolean started = !now.isBefore(dealStartAt);
        boolean notEnded = now.isBefore(dealEndAt);
        return started && notEnded;
    }

    public Long getDisplayPrice(LocalDateTime now) {
        return isDealActive(now) ? this.dealPrice  : this.regularPrice;
    }

    public String getDisplayUnit(LocalDateTime now) {
        return isDealActive(now) && dealUnit != null ? this.dealUnit : this.regularUnit;
    }

    public void changeHidden(boolean hidden) { this.hidden = hidden; }
    public void hide()   { this.hidden = true; }
    public void unhide() { this.hidden = false; }

    public void endDealNow(LocalDateTime now) {
        if (dealStartAt == null || dealEndAt == null) {
            this.dealActive = false;
            return;
        }

        if(!now.isBefore(dealEndAt)) {
            this.dealActive = false;
            return;
        }

        this.dealEndAt = now;
        this.dealActive = false;
    }
}
