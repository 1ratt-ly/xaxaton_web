package com.hackaton.website.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private BigDecimal subtotal;

    private BigDecimal compositeTaxRate;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;

    // розбивка
    @Column(columnDefinition = "TEXT")
    private String breakdown;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}