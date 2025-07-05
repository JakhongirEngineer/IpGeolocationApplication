package com.jakhongir.gelocation.service;

import com.jakhongir.gelocation.exception.ProviderUnavailableException;
import com.jakhongir.gelocation.properties.GeolocationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderSelectionService {
    private final List<IpGeolocationProvider> providers;
    private final GeolocationProperties properties;

    public IpGeolocationProvider selectProvider() {
        String primaryProviderName = properties.getProvider().getPrimary();
        Optional<IpGeolocationProvider> primaryProvider = findProvider(
                p -> p.getProviderName().equals(primaryProviderName)
        );

        if (primaryProvider.isPresent() && primaryProvider.get().isAvailable()) {
            log.debug("Using primary provider: {}", primaryProviderName);
            return primaryProvider.get();
        }

        log.warn("Primary provider {} is not available, trying fallback providers", primaryProviderName);

        Map<String, String> fallbackOrder = properties.getProvider().getFallbackOrder();
        for (var entry : fallbackOrder.entrySet()) {
            String fallbackProviderName = entry.getValue();
            Optional<IpGeolocationProvider> fallbackProvider = findProvider(p -> p.getProviderName().equals(fallbackProviderName));
            if (fallbackProvider.isPresent() && fallbackProvider.get().isAvailable()) {
                log.info("Using fallback provider: {}", fallbackProviderName);
                return fallbackProvider.get();
            }
        }

        throw new ProviderUnavailableException("No geolocation providers are currently available");
    }

    private Optional<IpGeolocationProvider> findProvider(Predicate<IpGeolocationProvider> filter) {
        return providers.stream()
                .filter(filter)
                .findFirst();
    }
}
