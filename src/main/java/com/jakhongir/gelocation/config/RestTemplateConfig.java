package com.jakhongir.gelocation.config;

import com.jakhongir.gelocation.properties.GeolocationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {
    private final GeolocationProperties properties;

    @Bean
    public RestTemplate restTemplateWithHttpClient() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(properties.getProvider().getFreeipapi().getTimeoutSeconds()));
        factory.setReadTimeout(Duration.ofSeconds(properties.getProvider().getFreeipapi().getTimeoutSeconds()));

        return new RestTemplate(factory);
    }
}
