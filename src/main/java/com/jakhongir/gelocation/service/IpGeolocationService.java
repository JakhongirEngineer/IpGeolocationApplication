package com.jakhongir.gelocation.service;

import com.jakhongir.gelocation.exception.InvalidIpAddressException;
import com.jakhongir.gelocation.model.IpLocationResponse;
import com.jakhongir.gelocation.util.IPv4ValidatorRegex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IpGeolocationService {
    private final ProviderSelectionService providerSelectionService;
    private final RateLimitService rateLimitService;

    @Cacheable(value = "ipLocationCache", key = "#ipAddress")
    public IpLocationResponse getLocationData(String ipAddress) {
        log.info("Processing request for IP: {}", ipAddress);
        if (!IPv4ValidatorRegex.isValid(ipAddress)) {
            throw new InvalidIpAddressException("Invalid IP v4 address format: " + ipAddress);
        }

        IpGeolocationProvider provider = providerSelectionService.selectProvider();

        rateLimitService.acquirePermit(provider.getProviderName());

        IpLocationResponse response = provider.getLocationData(ipAddress);

        log.info("Successfully retrieved location data for IP: {} using provider: {}",
                ipAddress, provider.getProviderName());

        return response;
    }
}
