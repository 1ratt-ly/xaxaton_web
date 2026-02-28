package com.hackaton.website.config;

import com.hackaton.website.entity.TaxRate;
import com.hackaton.website.repository.TaxRateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Objects;

@Component
public class DataLoader implements CommandLineRunner {

    private final TaxRateRepository taxRateRepository;

    public DataLoader(TaxRateRepository taxRateRepository) {
        this.taxRateRepository = taxRateRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (taxRateRepository.count() > 0) return;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream("/taxes.csv"))))) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                TaxRate tax = new TaxRate();
                tax.setCounty(data[0].trim());
                tax.setStateTax(new BigDecimal(data[1].trim()));
                tax.setLocalTax(new BigDecimal(data[2].trim()));
                tax.setTotalTax(new BigDecimal(data[3].trim()));
                taxRateRepository.save(tax);
            }
            System.out.println("Податки NY State успішно завантажені");
        } catch (Exception e) {
            System.err.println("Помилка завантаження" + e.getMessage());
        }
    }
}