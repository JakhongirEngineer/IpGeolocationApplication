package com.jakhongir.gelocation.service;

import com.jakhongir.gelocation.exception.ExternalApiException;
import com.jakhongir.gelocation.model.FreeIpApiResponse;
import com.jakhongir.gelocation.model.IpLocationResponse;
import com.jakhongir.gelocation.properties.GeolocationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeIpApiProvider implements IpGeolocationProvider {
    private static final String PROVIDER_NAME = "freeipapi";
    private final RestTemplate restTemplate;
    private final GeolocationProperties properties;
    private final String baseUrl = properties.getProvider().getFreeipapi().getBaseUrl();

    @Override
    public IpLocationResponse getLocationData(String ipAddress) {
       try {
           log.info("Querying FreeIpAPI for IP: {}", ipAddress);
           String url = baseUrl + "/" + ipAddress;
           FreeIpApiResponse response = restTemplate.getForObject(url, FreeIpApiResponse.class);

           if (response == null) {
               throw new ExternalApiException("Empty response from FreeIpAPI");
           }
           return mapToIpLocationResponse(response);
       } catch (Exception e) {
           log.error("Error querying FreeIpAPI for IP {}: {}", ipAddress, e.getMessage());
           throw new ExternalApiException("Failed to retrieve location data from FreeIpAPI");
       }
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean isAvailable() {
        try {
            // Let's not ping as it uses the free quota
            return true;
        } catch (Exception e) {
            log.warn("FreeIpAPI provider is unavailable: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public int getPriority() {
        // 1 -> the highest
        // 2 -> second highest
        return 1;
    }

    private IpLocationResponse mapToIpLocationResponse(FreeIpApiResponse freeIpApiResponse) {
        return new IpLocationResponse(
                freeIpApiResponse.ipAddress(),
                freeIpApiResponse.continent(),
                freeIpApiResponse.countryName(),
                freeIpApiResponse.regionName(),
                freeIpApiResponse.cityName(),
                freeIpApiResponse.latitude(),
                freeIpApiResponse.longitude(),
                LocalDateTime.now(),
                getProviderName()
        );
    }
}
