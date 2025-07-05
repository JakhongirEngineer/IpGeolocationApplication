package com.jakhongir.gelocation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDateTime;

public record IpLocationResponse(
        @JsonProperty("IpAddress") String ipAddress,
        @JsonProperty("Continent") String continent,
        @JsonProperty("Country") String country,
        @JsonProperty("Region") String region,
        @JsonProperty("City") String city,
        @JsonProperty("Latitude") Double latitude,
        @JsonProperty("Longitude") Double longitude,
        LocalDateTime cachedAt,
        String provider
) implements Serializable {
}
