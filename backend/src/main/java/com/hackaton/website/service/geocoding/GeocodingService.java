package com.hackaton.website.service.geocoding;

public interface GeocodingService {

    /**
     * @return GeoResult (stateCode + countyName)
     */
    GeoResult getCountyByCoordinates(double lat, double lon);

    record GeoResult(String stateCode, String countyName) {}
}