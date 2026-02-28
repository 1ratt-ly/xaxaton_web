package com.hackaton.website.service.tax;

import java.math.BigDecimal;

public record TaxCalculationResult(
        String stateCode,
        String countyName,
        BigDecimal stateRate,
        BigDecimal countyRate,
        BigDecimal cityRate,
        BigDecimal specialRate,
        BigDecimal compositeTaxRate,
        BigDecimal taxAmount,
        BigDecimal totalAmount
) {}