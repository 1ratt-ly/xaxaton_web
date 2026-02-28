package com.hackaton.website.config;

import com.hackaton.website.entity.TaxRate;
import com.hackaton.website.repository.TaxRateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class DataLoader implements CommandLineRunner {

    private final TaxRateRepository taxRateRepository;

    public DataLoader(TaxRateRepository taxRateRepository) {
        this.taxRateRepository = taxRateRepository;
    }

    @Override
    public void run(String... args) {
        if (taxRateRepository.count() > 0) return;

        String[] candidates = {"ny_taxes.csv", "taxes.csv"};
        InputStream is = null;
        String used = null;

        for (String name : candidates) {
            is = getClass().getClassLoader().getResourceAsStream(name);
            if (is != null) { used = name; break; }
        }

        if (is == null) {
            System.err.println("DataLoader: не найден файл налогов в resources (ожидали ny_taxes.csv или taxes.csv)");
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String header = br.readLine();
            if (header == null) return;

            String[] headers = header.split("\\s*,\\s*");
            Map<String, Integer> idx = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                idx.put(headers[i].trim().toLowerCase(Locale.ROOT), i);
            }

            // обязательный county
            int countyIdx = pick(idx, "county", "county_name", "countyname");
            if (countyIdx < 0) {
                System.err.println("DataLoader: нет колонки county в " + used);
                return;
            }

            // разные схемы колонок
            Integer stateRateIdx   = pick(idx, "state_rate", "state", "state_tax", "statetax");
            Integer countyRateIdx  = pick(idx, "county_rate", "county", "local_tax", "localtax");
            Integer specialRateIdx = pick(idx, "special_rate", "special", "specialtax");
            Integer totalIdx       = pick(idx, "total_tax", "total_rate", "total", "totaltax");

            String line;
            int loaded = 0;

            while ((line = br.readLine()) != null) {
                String raw = line.trim();
                if (raw.isEmpty()) continue;

                String[] cols = raw.split("\\s*,\\s*");
                if (cols.length <= countyIdx) continue;

                String county = cols[countyIdx].trim();
                if (county.isBlank()) continue;
                if (county.endsWith(" County")) county = county.substring(0, county.length() - " County".length()).trim();

                BigDecimal stateRate  = getRate(cols, stateRateIdx);
                BigDecimal countyRate = getRate(cols, countyRateIdx);
                BigDecimal specialRate = getRate(cols, specialRateIdx);

                // если special нет, но есть total — вычислим
                if (specialRate == null && totalIdx != null && totalIdx >= 0 && totalIdx < cols.length) {
                    BigDecimal total = parseRate(cols[totalIdx]);
                    if (total != null && stateRate != null && countyRate != null) {
                        BigDecimal calc = total.subtract(stateRate).subtract(countyRate);
                        if (calc.compareTo(BigDecimal.ZERO) < 0) calc = BigDecimal.ZERO;
                        specialRate = calc;
                    }
                }

                // если каких-то ставок нет — считаем их 0
                if (stateRate == null) stateRate = BigDecimal.ZERO;
                if (countyRate == null) countyRate = BigDecimal.ZERO;
                if (specialRate == null) specialRate = BigDecimal.ZERO;

                TaxRate t = new TaxRate();
                t.setCountyName(county);
                t.setStateRate(stateRate);
                t.setCountyRate(countyRate);
                t.setSpecialRate(specialRate);

                taxRateRepository.save(t);
                loaded++;
            }

            System.out.println("DataLoader: налоги успешно загружены из " + used + ". rows=" + loaded);

        } catch (Exception e) {
            System.err.println("DataLoader: ошибка загрузки налогов: " + e.getMessage());
        }
    }

    private static Integer pick(Map<String, Integer> idx, String... names) {
        for (String n : names) {
            Integer v = idx.get(n.toLowerCase(Locale.ROOT));
            if (v != null) return v;
        }
        return -1;
    }

    private static BigDecimal getRate(String[] cols, Integer i) {
        if (i == null || i < 0 || i >= cols.length) return null;
        return parseRate(cols[i]);
    }

    /**
     * Поддерживает:
     *  - 0.08875 (доля)
     *  - 8.875 (проценты)
     *  - 8.875% (проценты)
     */
    private static BigDecimal parseRate(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;

        boolean percent = t.endsWith("%");
        if (percent) t = t.substring(0, t.length() - 1).trim();

        BigDecimal v;
        try {
            v = new BigDecimal(t);
        } catch (Exception e) {
            return null;
        }

        // если > 1 — считаем что это проценты
        if (percent || v.compareTo(BigDecimal.ONE) > 0) {
            v = v.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
        }

        return v.setScale(6, RoundingMode.HALF_UP);
    }
}