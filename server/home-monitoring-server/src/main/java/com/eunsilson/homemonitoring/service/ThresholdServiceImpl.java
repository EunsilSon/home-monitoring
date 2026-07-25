package com.eunsilson.homemonitoring.service;

import com.eunsilson.homemonitoring.domain.dto.ThresholdRequest;
import com.eunsilson.homemonitoring.domain.dto.ThresholdResponse;
import com.eunsilson.homemonitoring.domain.dto.ThresholdResult;
import com.eunsilson.homemonitoring.domain.entity.SensorDataEntity;
import com.eunsilson.homemonitoring.domain.entity.ThresholdEntity;
import com.eunsilson.homemonitoring.repository.ThresholdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ThresholdServiceImpl implements ThresholdService {

    private static final long NOTIFICATION_COOLDOWN_MINUTES = 10;

    private final ThresholdRepository thresholdRepository;

    private final Map<UUID, Boolean> exceededStates = new ConcurrentHashMap<>();
    private final Map<UUID, Instant> lastNotificationTimes = new ConcurrentHashMap<>();

    @Override
    public ThresholdResponse getThreshold(UUID deviceId) {
        return thresholdRepository.findById(deviceId)
                .map(ThresholdResponse::from)
                .orElse(null);
    }

    @Override
    @Transactional
    public ThresholdResponse saveThreshold(UUID deviceId, ThresholdRequest request) {
        Instant now = Instant.now();

        Optional<ThresholdEntity> existing = thresholdRepository.findById(deviceId);

        ThresholdEntity entity;
        if (existing.isPresent()) {
            entity = existing.get();
            entity.update(
                    request.temperatureMin(),
                    request.temperatureMax(),
                    request.humidityMin(),
                    request.humidityMax(),
                    request.heatIndexMin(),
                    request.heatIndexMax(),
                    request.slackEnabled(),
                    now
            );
        } else {
            entity = new ThresholdEntity(
                    deviceId,
                    request.temperatureMin(),
                    request.temperatureMax(),
                    request.humidityMin(),
                    request.humidityMax(),
                    request.heatIndexMin(),
                    request.heatIndexMax(),
                    request.slackEnabled(),
                    now,
                    now
            );
        }

        ThresholdEntity saved = thresholdRepository.save(entity);
        return ThresholdResponse.from(saved);
    }

    @Override
    public ThresholdResult evaluateThreshold(SensorDataEntity sensorData) {
        UUID deviceId = sensorData.getDeviceId();
        Optional<ThresholdEntity> thresholdOpt = thresholdRepository.findById(deviceId);

        if (thresholdOpt.isEmpty()) {
            return new ThresholdResult(false, java.util.List.of());
        }

        ThresholdEntity threshold = thresholdOpt.get();

        if (threshold.getSlackEnabled() == null || !threshold.getSlackEnabled()) {
            return new ThresholdResult(false, java.util.List.of());
        }

        return ThresholdResult.builder()
                .check("Temperature", sensorData.getTemperature(),
                        threshold.getTemperatureMin(), threshold.getTemperatureMax())
                .check("Humidity", sensorData.getHumidity(),
                        threshold.getHumidityMin(), threshold.getHumidityMax())
                .check("Heat Index", sensorData.getHeatIndex(),
                        threshold.getHeatIndexMin(), threshold.getHeatIndexMax())
                .build();
    }

    @Override
    public boolean shouldSendNotification(UUID deviceId, boolean exceeded) {
        if (!exceeded) {
            exceededStates.remove(deviceId);
            return false;
        }

        Boolean wasExceeded = exceededStates.get(deviceId);
        Instant lastSent = lastNotificationTimes.get(deviceId);
        Instant now = Instant.now();

        if (wasExceeded == null || !wasExceeded) {
            exceededStates.put(deviceId, true);
            lastNotificationTimes.put(deviceId, now);
            return true;
        }

        if (lastSent != null &&
                ChronoUnit.MINUTES.between(lastSent, now) >= NOTIFICATION_COOLDOWN_MINUTES) {
            lastNotificationTimes.put(deviceId, now);
            return true;
        }

        return false;
    }
}
