package com.hackaton.website.dto;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderRequest {
    private Double latitude;
    private Double longitude;
    private BigDecimal subtotal;
}