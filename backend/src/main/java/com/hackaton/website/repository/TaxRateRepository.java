package com.hackaton.website.repository;
import com.hackaton.website.entity.TaxRate;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TaxRateRepository extends JpaRepository<TaxRate, String> {}