package com.jakhongir.gelocation.controller;

import com.jakhongir.gelocation.model.IpLocationResponse;
import com.jakhongir.gelocation.service.IpGeolocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class IpGeolocationController {
    private final IpGeolocationService ipGeolocationService;

    @GetMapping("/location/{ipAddress}")
    public ResponseEntity<IpLocationResponse> getLocationData(@PathVariable String ipAddress) {
        log.info("Received request for IP location: {}", ipAddress);
        IpLocationResponse response = ipGeolocationService.getLocationData(ipAddress);
        return ResponseEntity.ok(response);
    }

}
