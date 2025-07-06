package com.jakhongir.gelocation.unit;

import com.jakhongir.gelocation.model.IpLocationResponse;
import com.jakhongir.gelocation.service.IpGeolocationProvider;
import com.jakhongir.gelocation.service.IpGeolocationService;
import com.jakhongir.gelocation.service.ProviderSelectionService;
import com.jakhongir.gelocation.service.RateLimitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class IpGeolocationServiceTest {

    @Mock
    private ProviderSelectionService providerSelectionService;

    @Mock
    private RateLimitService rateLimitService;

    @Mock
    private IpGeolocationProvider mockProvider;

    @InjectMocks
    private IpGeolocationService ipGeolocationService;

    private final String validIpAddress = "192.168.1.1";
    private final String invalidIpAddress = "invalid.ip.address";
    private final String providerName = "TestProvider";

    @BeforeEach
    void setUp() {
        ipGeolocationService = new IpGeolocationService(providerSelectionService, rateLimitService);
    }

    @Test
    void shouldReturnLocationDataForValidIpAddress() {
        // Given
        IpLocationResponse expectedResponse = createMockResponse();

        when(providerSelectionService.selectProvider()).thenReturn(mockProvider);
        when(mockProvider.getProviderName()).thenReturn(providerName);
        when(mockProvider.getLocationData(validIpAddress)).thenReturn(expectedResponse);

        // When
        IpLocationResponse actualResponse = ipGeolocationService.getLocationData(validIpAddress);

        // Then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        verify(rateLimitService).acquirePermit(providerName);
        verify(mockProvider).getLocationData(validIpAddress);
    }

    private IpLocationResponse createMockResponse() {
        return new IpLocationResponse(
                validIpAddress,
                "Asia",
                "Uzbekistan",
                "Central Asia",
                "Tashkent",
                41.123,
                45.123,
                LocalDateTime.now(),
                providerName
        );
    }

}
