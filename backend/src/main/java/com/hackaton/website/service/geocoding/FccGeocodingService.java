package com.hackaton.website.service.geocoding;

import com.fasterxml.jackson.databind.JsonNode;
import com.hackaton.website.exception.GeocodingException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FccGeocodingService implements GeocodingService {

    private final RestTemplate restTemplate;
    private final Map<String, GeoResult> cache = new ConcurrentHashMap<>();

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
            JsonNode root = restTemplate.getForObject(url, JsonNode.class);
            if (root == null) throw new GeocodingException("FCC returned empty response");

            String stateCode = text(root, "State", "code");
            String countyName = text(root, "County", "name");

            if (stateCode == null || stateCode.isBlank()) {
                throw new GeocodingException("FCC: cannot extract State.code");
            }
            if (countyName == null || countyName.isBlank()) {
                throw new GeocodingException("FCC: cannot extract County.name");
            }

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

    private static String text(JsonNode root, String obj, String field) {
        JsonNode n = root.path(obj).path(field);
        if (n.isMissingNode() || n.isNull()) return null;
        return n.asText();
    }

    private static String normalizeCounty(String raw) {
        String s = raw.trim();
        if (s.endsWith(" County")) s = s.substring(0, s.length() - " County".length()).trim();
        return s;
    }
}