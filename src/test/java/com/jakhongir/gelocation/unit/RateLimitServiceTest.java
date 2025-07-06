package com.jakhongir.gelocation.unit;

import com.jakhongir.gelocation.exception.RateLimitExceededException;
import com.jakhongir.gelocation.properties.GeolocationProperties;
import com.jakhongir.gelocation.service.RateLimitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimitServiceTest {

    @Mock
    private GeolocationProperties properties;

    @Mock
    private GeolocationProperties.RateLimitProperties rateLimitProperties;

    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        when(properties.getRateLimit()).thenReturn(rateLimitProperties);
        when(rateLimitProperties.getRequestsPerSecond()).thenReturn(2);
        when(rateLimitProperties.getMaxWaitTimeSeconds()).thenReturn(1);
        when(rateLimitProperties.getBucketCapacity()).thenReturn(5);

        rateLimitService = new RateLimitService(properties);
    }

    @Test
    void shouldAllowRequestsWithinRateLimit() {
        // Given
        String provider = "testProvider";

        // When & Then
        assertDoesNotThrow(() -> rateLimitService.acquirePermit(provider));
        assertDoesNotThrow(() -> rateLimitService.acquirePermit(provider));
    }

    @Test
    void shouldThrowExceptionWhenRateLimitExceeded() {
        // Given
        String provider = "testProvider";

        // Use all available permits quickly
        for (int i = 0; i < 5; i++) {
            rateLimitService.acquirePermit(provider);
        }

        // When & Then
        RateLimitExceededException exception = assertThrows(RateLimitExceededException.class,
                () -> rateLimitService.acquirePermit(provider));

        assertTrue(exception.getMessage().contains("Rate limit exceeded"));
        assertTrue(exception.getMessage().contains("testProvider"));
    }

    @Test
    void shouldHandleMultipleProvidersIndependently() {
        // Given
        String provider1 = "provider1";
        String provider2 = "provider2";

        // When & Then
        assertDoesNotThrow(() -> rateLimitService.acquirePermit(provider1));
        assertDoesNotThrow(() -> rateLimitService.acquirePermit(provider2));

        // Use all permits for provider1
        for (int i = 0; i < 4; i++) {
            rateLimitService.acquirePermit(provider1);
        }

        // provider1 should be rate limited but provider2 should still work
        assertThrows(RateLimitExceededException.class,
                () -> rateLimitService.acquirePermit(provider1));
        assertDoesNotThrow(() -> rateLimitService.acquirePermit(provider2));
    }

    @Test
    void shouldRefillTokensOverTime() throws InterruptedException {
        // Given
        String provider = "testProvider";

        // Use all available permits
        for (int i = 0; i < 5; i++) {
            rateLimitService.acquirePermit(provider);
        }

        // Should be rate limited now
        assertThrows(RateLimitExceededException.class,
                () -> rateLimitService.acquirePermit(provider));

        // Wait
        Thread.sleep(1000);

        // Should be able to acquire permit again
        assertDoesNotThrow(() -> rateLimitService.acquirePermit(provider));
    }

    @Test
    void shouldHandleConcurrentRequests() throws InterruptedException {
        // Given
        String provider = "testProvider";
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        // When
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    rateLimitService.acquirePermit(provider);
                    successCount.incrementAndGet();
                } catch (RateLimitExceededException e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        // Then
        assertEquals(threadCount, successCount.get() + failureCount.get());
        assertTrue(successCount.get() <= 5); // Should not exceed bucket capacity
        assertTrue(failureCount.get() >= 5); // Some requests should fail
    }
}