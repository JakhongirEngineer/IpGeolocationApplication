package com.jakhongir.gelocation.model;


import java.util.List;

public record FreeIpApiResponse(
        int ipVersion,
        String ipAddress,
        double latitude,
        double longitude,
        String countryName,
        String countryCode,
        String capital,
        List<Integer> phoneCodes,
        List<String> timeZones,
        String cityName,
        String regionName,
        String continent,
        String continentCode,
        List<String> currencies,
        List<String> languages,
        String asn,
        String asnOrganization
) {
}
