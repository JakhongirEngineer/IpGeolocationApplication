package com.jakhongir.gelocation.unit;


import com.jakhongir.gelocation.util.IPv4ValidatorRegex;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IPv4ValidatorRegexTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "192.168.10.11",
            "127.5.6.1",
            "255.255.255.255",
            "0.0.0.0",
            "8.8.8.8",
            "172.16.0.1",
            "10.10.10.10"
    })
    void shouldReturnTrueForValidIpAddresses(String ipAddress) {
        assertTrue(IPv4ValidatorRegex.isValid(ipAddress));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "256.1.1.1",
            "192.168.1.256",
            "192.168.1",
            "192.168.1.1.1",
            "192.168.-1.1",
            "192.168.1.1a",
            "192.168..1",
            "192.168.1.",
            ".192.168.1.1",
            "192,168,1,1",
            "192.168.1.1 ",
            " 192.168.1.1"
    })
    void shouldReturnFalseForInvalidIpAddresses(String ipAddress) {
        assertFalse(IPv4ValidatorRegex.isValid(ipAddress));
    }

    @Test
    void shouldReturnFalseForNullIpAddress() {
        assertFalse(IPv4ValidatorRegex.isValid(null));
    }

    @Test
    void shouldReturnFalseForEmptyString() {
        assertFalse(IPv4ValidatorRegex.isValid(""));
    }
}
