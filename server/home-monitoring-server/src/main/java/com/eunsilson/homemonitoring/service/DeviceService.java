package com.eunsilson.homemonitoring.service;

import com.eunsilson.homemonitoring.domain.dto.DeviceStatusResponse;

public interface DeviceService {
    void recordHeartbeat();

    DeviceStatusResponse getStatus();
}
