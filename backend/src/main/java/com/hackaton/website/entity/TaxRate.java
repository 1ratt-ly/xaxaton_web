package com.hackaton.website.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "tax_rates",
        indexes = @Index(name = "idx_tax_rates_county", columnList = "countyName", unique = true))
@Data
public class TaxRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128, unique = true)
    private String countyName;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal stateRate;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal countyRate;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal specialRate;
}