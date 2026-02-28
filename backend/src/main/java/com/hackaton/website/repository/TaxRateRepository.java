package com.hackaton.website.repository;

import com.hackaton.website.entity.TaxRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaxRateRepository extends JpaRepository<TaxRate, Long> {
    Optional<TaxRate> findByCountyNameIgnoreCase(String countyName);
}