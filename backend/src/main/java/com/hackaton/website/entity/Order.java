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

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal subtotal;

    // Geo результат
    @Column(length = 16)
    private String stateCode;
    @Column(length = 128)
    private String countyName;

    @Column(precision = 19, scale = 6)
    private BigDecimal stateRate;
    @Column(precision = 19, scale = 6)
    private BigDecimal countyRate;
    @Column(precision = 19, scale = 6)
    private BigDecimal cityRate;
    @Column(precision = 19, scale = 6)
    private BigDecimal specialRate;


    @Column(precision = 19, scale = 6)
    private BigDecimal compositeTaxRate;

    @Column(precision = 19, scale = 2)
    private BigDecimal taxAmount;

    @Column(precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "created_at")
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }
}