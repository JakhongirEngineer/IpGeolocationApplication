package com.jakhongir.gelocation.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Data
@Validated
@ConfigurationProperties(prefix = "geolocation")
public class GeolocationProperties {
    @NotNull
    private CacheProperties cache = new CacheProperties();

    @NotNull
    private RateLimitProperties rateLimit = new RateLimitProperties();

    @NotNull
    private ProviderProperties provider = new ProviderProperties();


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

    @Data
    public static class ProviderProperties {
        @NotBlank
        private String primary = "freeipapi";

        @NotNull
        private Map<String, String> fallbackOrder = Map.of(
                "1", "freeipapi"
        );

        @NotNull
        private GeolocationProperties.ProviderProperties.FreeIpApiProperties freeipapi = new FreeIpApiProperties();

        @Data
        public static class FreeIpApiProperties {
            @NotBlank
            private String baseUrl = "https://freeipapi.com/api/json";

            @Positive
            private int timeoutSeconds = 10;

            @Positive
            private int retryAttempts = 3;
        }
    }
}
