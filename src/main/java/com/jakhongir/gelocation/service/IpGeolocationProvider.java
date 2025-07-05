package com.jakhongir.gelocation.service;

import com.jakhongir.gelocation.model.IpLocationResponse;

public interface IpGeolocationProvider {
    IpLocationResponse getLocationData(String ipAddress);
    String getProviderName();
    boolean isAvailable();
    int getPriority();
}
