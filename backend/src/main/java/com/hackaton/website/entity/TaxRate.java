package com.hackaton.website.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "tax_rates")
@Data
public class TaxRate {
    @Id
    private String county;
    private BigDecimal stateTax;
    private BigDecimal localTax;
    private BigDecimal totalTax;
}