package com.hackaton.website.service.geocoding;

import com.hackaton.website.exception.GeocodingException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FccGeocodingService implements GeocodingService {

    private final RestTemplate restTemplate;

    // простой кеш, чтобы не дёргать FCC одинаковыми координатами
    private final Map<String, GeoResult> cache = new ConcurrentHashMap<>();

    // Достаём State.code и County.name из JSON
    private static final Pattern STATE_CODE = Pattern.compile(
            "\"State\"\\s*:\\s*\\{.*?\"code\"\\s*:\\s*\"([^\"]+)\"",
            Pattern.DOTALL
    );
    private static final Pattern COUNTY_NAME = Pattern.compile(
            "\"County\"\\s*:\\s*\\{.*?\"name\"\\s*:\\s*\"([^\"]+)\"",
            Pattern.DOTALL
    );

    public FccGeocodingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public GeoResult getCountyByCoordinates(double lat, double lon) {
        String key = String.format("%.6f,%.6f", lat, lon);
        GeoResult cached = cache.get(key);
        if (cached != null) return cached;

        String url = "https://geo.fcc.gov/api/census/block/find"
                + "?latitude=" + lat
                + "&longitude=" + lon
                + "&format=json";

        try {
            String body = restTemplate.getForObject(url, String.class);
            if (body == null || body.isBlank()) {
                throw new GeocodingException("FCC returned empty response");
            }

            String stateCode = extract(body, STATE_CODE, "State.code");
            String countyName = extract(body, COUNTY_NAME, "County.name");

            countyName = normalizeCounty(countyName);

            GeoResult result = new GeoResult(stateCode.trim(), countyName);
            cache.put(key, result);
            return result;

        } catch (GeocodingException e) {
            throw e;
        } catch (Exception e) {
            throw new GeocodingException("FCC request failed: " + e.getMessage(), e);
        }
    }

    private static String extract(String json, Pattern p, String field) {
        Matcher m = p.matcher(json);
        if (!m.find()) {
            throw new GeocodingException("FCC: cannot extract " + field);
        }
        String val = m.group(1);
        if (val == null || val.isBlank()) {
            throw new GeocodingException("FCC: empty " + field);
        }
        return val;
    }

    private static String normalizeCounty(String raw) {
        String s = raw.trim();
        if (s.endsWith(" County")) s = s.substring(0, s.length() - " County".length()).trim();
        return s;
    }
}