package com.hackaton.website.service.csv;

import com.hackaton.website.exception.CsvImportException;
import com.hackaton.website.service.tax.TaxEngineService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
public class CsvImportService {

    private final TaxEngineService taxEngineService;
    private final ExecutorService importExecutor;

    public CsvImportService(TaxEngineService taxEngineService, ExecutorService importExecutor) {
        this.taxEngineService = taxEngineService;
        this.importExecutor = importExecutor;
    }

    public void importCsv(InputStream inputStream, ImportJobStatus progress) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String headerLine = br.readLine();
            if (headerLine == null) throw new CsvImportException("CSV is empty");

            String[] headers = split(headerLine);
            Map<String, Integer> idx = indexMap(headers);

            int latIdx = idx.getOrDefault("latitude", -1);
            int lonIdx = idx.getOrDefault("longitude", -1);
            int subIdx = idx.getOrDefault("subtotal", -1);
            int tsIdx  = idx.getOrDefault("timestamp", -1);

            if (latIdx < 0 || lonIdx < 0 || subIdx < 0) {
                throw new CsvImportException("CSV must contain columns: latitude, longitude, subtotal");
            }

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            String line;
            int lineNo = 1;

            while ((line = br.readLine()) != null) {
                lineNo++;
                String raw = line.trim();
                if (raw.isEmpty()) continue;

                String[] cols = split(raw);
                if (cols.length <= Math.max(Math.max(latIdx, lonIdx), subIdx)) {
                    progress.incFailed("Line " + lineNo + ": not enough columns");
                    continue;
                }

                progress.incTotal();
                final int ln = lineNo;

                CompletableFuture<Void> f = CompletableFuture.runAsync(() -> {
                    try {
                        double lat = parseDouble(cols[latIdx], "latitude");
                        double lon = parseDouble(cols[lonIdx], "longitude");
                        BigDecimal subtotal = parseMoney(cols[subIdx]);

                        LocalDateTime ts = null;
                        if (tsIdx >= 0 && tsIdx < cols.length) {
                            ts = parseTimestamp(cols[tsIdx]);
                        }

                        taxEngineService.createAndSaveOrder(lat, lon, subtotal, ts);
                        progress.incSuccess();
                    } catch (Exception e) {
                        progress.incFailed("Line " + ln + ": " + e.getMessage());
                    }
                }, importExecutor);

                futures.add(f);

                if (futures.size() >= 300) {
                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                    futures.clear();
                }
            }

            if (!futures.isEmpty()) {
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            }

        } catch (CsvImportException e) {
            throw e;
        } catch (Exception e) {
            throw new CsvImportException("CSV import failed: " + e.getMessage(), e);
        }
    }

    private static Map<String, Integer> indexMap(String[] headers) {
        Map<String, Integer> m = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            m.put(headers[i].trim().toLowerCase(Locale.ROOT), i);
        }
        return m;
    }

    private static String[] split(String line) {
        return line.split("\\s*,\\s*");
    }

    private static double parseDouble(String s, String field) {
        try {
            double v = Double.parseDouble(s.trim());
            if ("latitude".equals(field) && (v < -90 || v > 90)) throw new IllegalArgumentException("latitude out of range");
            if ("longitude".equals(field) && (v < -180 || v > 180)) throw new IllegalArgumentException("longitude out of range");
            return v;
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid " + field + ": " + s);
        }
    }

    private static BigDecimal parseMoney(String s) {
        try {
            BigDecimal v = new BigDecimal(s.trim());
            if (v.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("subtotal must be >= 0");
            return v.setScale(2, java.math.RoundingMode.HALF_UP);
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid subtotal: " + s);
        }
    }

    private static LocalDateTime parseTimestamp(String s) {
        String t = s.trim();
        if (t.isEmpty()) return null;
        try {
            return LocalDateTime.parse(t, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            return null;
        }
    }
}