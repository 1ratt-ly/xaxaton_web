package com.hackaton.website.service.geocoding;

public interface GeocodingService {
    GeoResult getCountyByCoordinates(double lat, double lon);

    record GeoResult(String stateCode, String countyName) {}
}