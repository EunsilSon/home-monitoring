package com.eunsilson.homemonitoring.service;

import com.eunsilson.homemonitoring.domain.dto.DeviceStatusResponse;

import java.util.UUID;

public interface DeviceService {
    void recordSensorDataReceived(UUID deviceId);

    DeviceStatusResponse getStatus(UUID deviceId);
}
