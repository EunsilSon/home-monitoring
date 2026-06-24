package com.eunsilson.homemonitoring.service;

import com.eunsilson.homemonitoring.domain.dto.SensorDataRequest;
import com.eunsilson.homemonitoring.domain.entity.SensorDataEntity;
import com.eunsilson.homemonitoring.domain.entity.SensorLatestEntity;
import com.eunsilson.homemonitoring.repository.SensorDataRepository;
import com.eunsilson.homemonitoring.repository.SensorLatestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.BinaryOperator;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SensorServiceImpl implements SensorService {
    private final SensorDataRepository sensorDataRepository;
    private final SensorLatestRepository sensorLatestRepository;
    private final DeviceService deviceService;

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

        return latestDataByDevice.values().stream()
                .map(this::updateLatest)
                .allMatch(Boolean::booleanValue);
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
