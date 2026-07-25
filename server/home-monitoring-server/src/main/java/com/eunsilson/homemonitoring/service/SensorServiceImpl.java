package com.eunsilson.homemonitoring.service;

import com.eunsilson.homemonitoring.domain.dto.SensorDataRequest;
import com.eunsilson.homemonitoring.domain.dto.ThresholdResult;
import com.eunsilson.homemonitoring.domain.entity.SensorDataEntity;
import com.eunsilson.homemonitoring.domain.entity.SensorLatestEntity;
import com.eunsilson.homemonitoring.repository.SensorDataRepository;
import com.eunsilson.homemonitoring.repository.SensorLatestRepository;
import com.eunsilson.homemonitoring.slack.SlackService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SensorServiceImpl implements SensorService {

    private static final Logger log = LoggerFactory.getLogger(SensorServiceImpl.class);

    private final SensorDataRepository sensorDataRepository;
    private final SensorLatestRepository sensorLatestRepository;
    private final DeviceService deviceService;
    private final ThresholdService thresholdService;
    private final SlackService slackService;

    public SensorLatestEntity getLatest(UUID deviceId) {
        return sensorLatestRepository.findByDeviceId(deviceId);
    }

    @Override
    @Transactional
    public boolean saveSensorDataAndLatestUpdate(List<SensorDataRequest> requests) {
        if (requests.isEmpty()) {
            return false;
        }

        List<SensorDataEntity> entities = requests.stream()
                .map(SensorDataRequest::toSensorData)
                .toList();

        entities.stream()
                .map(SensorDataEntity::getDeviceId)
                .distinct()
                .forEach(deviceService::recordSensorDataReceived);

        sensorDataRepository.saveAll(entities);

        var latestDataByDevice = entities.stream()
                .collect(Collectors.toMap(
                        SensorDataEntity::getDeviceId,
                        Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparing(SensorDataEntity::getRecordedAt))
                ));

        boolean allUpdated = latestDataByDevice.values().stream()
                .map(this::updateLatest)
                .allMatch(Boolean::booleanValue);

        checkThresholdsAndNotify(latestDataByDevice.values().stream().toList());

        return allUpdated;
    }

    private void checkThresholdsAndNotify(List<SensorDataEntity> latestDataList) {
        for (SensorDataEntity sensorData : latestDataList) {
            try {
                ThresholdResult result = thresholdService.evaluateThreshold(sensorData);
                UUID deviceId = sensorData.getDeviceId();

                if (thresholdService.shouldSendNotification(deviceId, result.exceeded())) {
                    if (result.exceeded()) {
                        slackService.sendThresholdAlert(sensorData, result);
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to evaluate threshold or send notification for device {}: {}",
                        sensorData.getDeviceId(), e.getMessage());
            }
        }
    }

    @Override
    public boolean sendSlackAlertIfThresholdExceeded(UUID deviceId, List<SensorDataRequest> requests) {
        if (requests.isEmpty()) {
            return false;
        }

        List<SensorDataEntity> entities = requests.stream()
                .map(SensorDataRequest::toSensorData)
                .toList();

        var latestDataByDevice = entities.stream()
                .collect(Collectors.toMap(
                        SensorDataEntity::getDeviceId,
                        Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparing(SensorDataEntity::getRecordedAt))
                ));

        checkThresholdsAndNotify(latestDataByDevice.values().stream().toList());
        return true;
    }

    private boolean updateLatest(SensorDataEntity latestData) {
        var result = sensorLatestRepository.upsert(
                latestData.getDeviceId(),
                latestData.getTemperature(),
                latestData.getHumidity(),
                latestData.getHeatIndex(),
                latestData.getRecordedAt(),
                Instant.now()
        );
        return result > 0;
    }
}
