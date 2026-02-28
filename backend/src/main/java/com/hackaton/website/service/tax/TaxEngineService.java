package com.hackaton.website.service.tax;

import com.hackaton.website.entity.Order;
import com.hackaton.website.entity.TaxRate;
import com.hackaton.website.exception.OutOfNyStateException;
import com.hackaton.website.exception.TaxRateNotFoundException;
import com.hackaton.website.repository.OrderRepository;
import com.hackaton.website.repository.TaxRateRepository;
import com.hackaton.website.service.geocoding.GeocodingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class TaxEngineService {

    private final GeocodingService geocodingService;
    private final TaxRateRepository taxRateRepository;
    private final OrderRepository orderRepository;

    public TaxEngineService(GeocodingService geocodingService,
                            TaxRateRepository taxRateRepository,
                            OrderRepository orderRepository) {
        this.geocodingService = geocodingService;
        this.taxRateRepository = taxRateRepository;
        this.orderRepository = orderRepository;
    }

    public TaxCalculationResult calculateTax(BigDecimal subtotal, double lat, double lon) {
        if (subtotal == null) throw new IllegalArgumentException("subtotal is null");
        if (subtotal.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("subtotal must be >= 0");

        GeocodingService.GeoResult geo = geocodingService.getCountyByCoordinates(lat, lon);

        if (!"NY".equalsIgnoreCase(geo.stateCode())) {
            throw new OutOfNyStateException("Point is outside NY. state=" + geo.stateCode());
        }

        TaxRate rate = taxRateRepository.findByCountyNameIgnoreCase(geo.countyName())
                .orElseThrow(() -> new TaxRateNotFoundException("Tax rate not found for county: " + geo.countyName()));

        BigDecimal composite = safe(rate.getStateRate())
                .add(safe(rate.getCountyRate()))
                .add(safe(rate.getSpecialRate()))
                .setScale(6, RoundingMode.HALF_UP);

        BigDecimal taxAmount = subtotal.multiply(composite).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.add(taxAmount).setScale(2, RoundingMode.HALF_UP);

        return new TaxCalculationResult(
                geo.stateCode(),
                geo.countyName(),
                safe(rate.getStateRate()),
                safe(rate.getCountyRate()),
                safe(rate.getSpecialRate()),
                composite,
                taxAmount,
                totalAmount
        );
    }

    public Order createAndSaveOrder(double lat, double lon, BigDecimal subtotal, LocalDateTime timestampOrNull) {
        TaxCalculationResult r = calculateTax(subtotal, lat, lon);

        Order o = new Order();
        o.setLatitude(lat);
        o.setLongitude(lon);
        o.setSubtotal(subtotal);

        o.setStateCode(r.stateCode());
        o.setCountyName(r.countyName());

        o.setStateRate(r.stateRate());
        o.setCountyRate(r.countyRate());
        o.setSpecialRate(r.specialRate());

        o.setCompositeTaxRate(r.compositeTaxRate());
        o.setTaxAmount(r.taxAmount());
        o.setTotalAmount(r.totalAmount());

        if (timestampOrNull != null) o.setTimestamp(timestampOrNull);

        return orderRepository.save(o);
    }

    private static BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}