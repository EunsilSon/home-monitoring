package com.eunsilson.homemonitoring.domain.dto;

import com.eunsilson.homemonitoring.domain.entity.SensorDataEntity;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record SensorDataRequest(
        @NotNull UUID deviceId,
        String temperature,
        String humidity,
        String heatIndex,
        String recordedAt
) {
    public SensorDataEntity toSensorData() {
        return new SensorDataEntity(
                deviceId,
                Float.parseFloat(temperature),
                Float.parseFloat(humidity),
                Float.parseFloat(heatIndex),
                Instant.parse(recordedAt)
        );
    }
}
