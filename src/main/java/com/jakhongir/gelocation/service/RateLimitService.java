package com.jakhongir.gelocation.service;

import com.jakhongir.gelocation.exception.RateLimitExceededException;
import com.jakhongir.gelocation.properties.GeolocationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


/**
 * Service to handle rate limiting for external API calls.
 * Implements a token bucket algorithm with configurable rate limits.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {
    private final GeolocationProperties geolocationProperties;
    private final ConcurrentHashMap<String, TokenBucket> providerBuckets = new ConcurrentHashMap<>();

    public void acquirePermit(String provider) {
        TokenBucket bucket = providerBuckets.computeIfAbsent(provider, k -> new TokenBucket(geolocationProperties.getRateLimit()));
        if (!bucket.tryAcquire()) {
            throw new RateLimitExceededException(
                    String.format("Rate limit exceeded for provider %s. Max wait time: %d seconds",
                            provider, geolocationProperties.getRateLimit().getMaxWaitTimeSeconds())
            );
        }
    }

    private class TokenBucket {
        private final Semaphore semaphore;
        private final long intervalMillis;
        private final int maxWaitTimeSeconds;
        private final int maxTokens;
        private volatile LocalDateTime lastRefill;

        public TokenBucket(GeolocationProperties.RateLimitProperties rateLimitProperties) {
            this.semaphore = new Semaphore(rateLimitProperties.getBucketCapacity());
            this.intervalMillis = 1000 / rateLimitProperties.getRequestsPerSecond();
            this.maxWaitTimeSeconds = rateLimitProperties.getMaxWaitTimeSeconds();
            this.maxTokens = rateLimitProperties.getBucketCapacity();
            this.lastRefill = LocalDateTime.now();
        }

        public boolean tryAcquire() {
            refillTokens();
            try {
                boolean acquired = semaphore.tryAcquire(maxWaitTimeSeconds, TimeUnit.SECONDS);
                if (!acquired) {
                    log.warn("Failed to acquire permit within {} seconds", maxWaitTimeSeconds);
                }
                return acquired;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Thread interrupted while waiting for rate limit permit");
                return false;
            }
        }

        private void refillTokens() {
            LocalDateTime now = LocalDateTime.now();
            long timeSinceLastRefill = ChronoUnit.MILLIS.between(lastRefill, now);

            if (timeSinceLastRefill >= intervalMillis) {
                int tokensToAdd = (int) (timeSinceLastRefill / intervalMillis);
                int currentTokens = semaphore.availablePermits();

                int tokensToRelease = Math.min(tokensToAdd, maxTokens - currentTokens);

                if (tokensToRelease > 0) {
                    semaphore.release(tokensToRelease);
                    log.debug("Released {} tokens for rate limiting", tokensToRelease);
                }
                lastRefill = now;
            }
        }
    }
}
