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
    private GeolocationProperties.CacheProperties cache = new CacheProperties();


    @Data
    public static class CacheProperties {
        @Positive
        private int ttlDays = 30;
        @Positive
        private int maxEntries = 100000;
    }
}
