package com.jakhongir.gelocation.integration;

import com.jakhongir.gelocation.model.IpLocationResponse;
import com.jakhongir.gelocation.service.IpGeolocationProvider;
import com.jakhongir.gelocation.service.IpGeolocationService;
import com.jakhongir.gelocation.service.ProviderSelectionService;
import com.jakhongir.gelocation.service.RateLimitService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Testcontainers
public class IpGeolocationServiceIntegrationTest {

    @Container
    private static final GenericContainer<?> REDIS_CONTAINER =
        new GenericContainer<>("redis:8.0.2")
                .withExposedPorts(6379)
                .withCommand("redis-server");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", REDIS_CONTAINER::getFirstMappedPort);
        registry.add("spring.cache.type", () -> "redis");
        registry.add("spring.cache.redis.time-to-live", () -> "600000");
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public ProviderSelectionService providerSelectionService() {
            return mock(ProviderSelectionService.class);
        }

        @Bean
        @Primary
        public RateLimitService rateLimitService() {
            return mock(RateLimitService.class);
        }

        @Bean
        @Primary
        public IpGeolocationProvider mockProvider() {
            return mock(IpGeolocationProvider.class);
        }
    }

    @Autowired
    private IpGeolocationService ipGeolocationService;

    @Autowired
    private ProviderSelectionService providerSelectionService;

    @Autowired
    private RateLimitService rateLimitService;

    @Autowired
    private IpGeolocationProvider mockProvider;

    @Test
    void getLocationData_CacheableAnnotation_CachesResults() {
        // Given
        String ipAddress = "192.168.2.1";
        String providerName = "TestProvider";
        IpLocationResponse expectedResponse = new IpLocationResponse(
                ipAddress,
                "Asia",
                "Uzbekistan",
                "Central Asia",
                "Tashkent",
                41.123,
                45.123,
                LocalDateTime.now(),
                providerName
        );

        when(mockProvider.getProviderName()).thenReturn(providerName);
        when(providerSelectionService.selectProvider()).thenReturn(mockProvider);
        when(mockProvider.getLocationData(ipAddress)).thenReturn(expectedResponse);

        // When - call twice
        ipGeolocationService.getLocationData(ipAddress);
        ipGeolocationService.getLocationData(ipAddress);

        // Then - provider should only be called once due to caching
        verify(mockProvider, times(1)).getLocationData(ipAddress);
        verify(rateLimitService, times(1)).acquirePermit(providerName);
    }
}