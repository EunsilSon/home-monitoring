package com.eunsilson.homemonitoring.domain.dto;

import com.eunsilson.homemonitoring.domain.entity.ThresholdEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ThresholdResponse(
        UUID deviceId,
        BigDecimal temperatureMin,
        BigDecimal temperatureMax,
        BigDecimal humidityMin,
        BigDecimal humidityMax,
        BigDecimal heatIndexMin,
        BigDecimal heatIndexMax,
        Boolean slackEnabled,
        Instant createdAt,
        Instant updatedAt
) {
    public static ThresholdResponse from(ThresholdEntity entity) {
        return new ThresholdResponse(
                entity.getDeviceId(),
                entity.getTemperatureMin(),
                entity.getTemperatureMax(),
                entity.getHumidityMin(),
                entity.getHumidityMax(),
                entity.getHeatIndexMin(),
                entity.getHeatIndexMax(),
                entity.getSlackEnabled(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
