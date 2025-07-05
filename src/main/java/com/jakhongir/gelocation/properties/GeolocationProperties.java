package com.jakhongir.gelocation.properties;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "geolocation")
public class GeolocationProperties {
    @NotNull
    private CacheProperties cache = new CacheProperties();

    @NotNull
    private RateLimitProperties rateLimit = new RateLimitProperties();


    @Data
    public static class CacheProperties {
        @Positive
        private int ttlDays = 30;
        @Positive
        private int maxEntries = 100000;
    }

    @Data
    public static class RateLimitProperties {
        @Positive
        private int requestsPerSecond = 1;
        @Positive
        private int maxWaitTimeSeconds = 30;
        @Positive
        private int bucketCapacity = 5;
    }
}
