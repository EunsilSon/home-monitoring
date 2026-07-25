package com.eunsilson.homemonitoring.service;

import com.eunsilson.homemonitoring.domain.dto.ThresholdRequest;
import com.eunsilson.homemonitoring.domain.dto.ThresholdResponse;
import com.eunsilson.homemonitoring.domain.dto.ThresholdResult;
import com.eunsilson.homemonitoring.domain.entity.SensorDataEntity;

import java.util.UUID;

public interface ThresholdService {
    ThresholdResponse getThreshold(UUID deviceId);

    ThresholdResponse saveThreshold(UUID deviceId, ThresholdRequest request);

    ThresholdResult evaluateThreshold(SensorDataEntity sensorData);

    boolean shouldSendNotification(UUID deviceId, boolean exceeded);
}
