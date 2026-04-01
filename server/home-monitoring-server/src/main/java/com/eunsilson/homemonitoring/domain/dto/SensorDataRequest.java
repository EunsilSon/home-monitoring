package com.eunsilson.homemonitoring.domain.dto;

import com.eunsilson.homemonitoring.domain.entity.SensorDataEntity;

import java.time.Instant;
import java.util.UUID;

public record SensorDataRequest(
        String temperature,
        String humidity,
        String heatIndex,
        String recordedAt
) {
    public SensorDataEntity toSensorData(UUID deviceId) {
        return new SensorDataEntity(
                deviceId,
                Float.parseFloat(temperature),
                Float.parseFloat(humidity),
                Float.parseFloat(heatIndex),
                Instant.parse(recordedAt)
        );
    }
}
