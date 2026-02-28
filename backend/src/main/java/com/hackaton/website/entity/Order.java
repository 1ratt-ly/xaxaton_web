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

    // тут потім податки будуть рахуватись
    private BigDecimal compositeTaxRate;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;

    @Column(name = "created_at")
    private LocalDateTime timestamp;

    // цей метод автоматично поставить поточний час при збереженні в БД
    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
