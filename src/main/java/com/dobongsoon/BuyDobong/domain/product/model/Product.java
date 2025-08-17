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
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockLevel stockLevel;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static Product create(Store store, String name, Long regularPrice, String unit, StockLevel stockLevel) {
        return Product.builder()
                .store(store)
                .name(name)
                .regularPrice(regularPrice)
                .unit(unit)
                .stockLevel(stockLevel)
                .build();
    }
}
