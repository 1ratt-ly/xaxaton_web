package com.hackaton.website.dto;

import com.hackaton.website.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        Double latitude,
        Double longitude,
        BigDecimal subtotal,
        BigDecimal stateRate,
        BigDecimal countyRate,
        BigDecimal cityRate,
        BigDecimal specialRate,
        BigDecimal compositeTaxRate,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        LocalDateTime timestamp
) {
    public static OrderResponse from(Order o) {
        return new OrderResponse(
                o.getId(),
                o.getLatitude(),
                o.getLongitude(),
                o.getSubtotal(),
                o.getStateRate(),
                o.getCountyRate(),
                o.getCityRate(),
                o.getSpecialRate(),
                o.getCompositeTaxRate(),
                o.getTaxAmount(),
                o.getTotalAmount(),
                o.getTimestamp()
        );
    }
}