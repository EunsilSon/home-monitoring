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
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SensorServiceImpl implements SensorService {
    private static final UUID DEVICE_ID = UUID.fromString("220aa852-ee70-4105-8cf5-98c23cb5e631");
    private final SensorDataRepository sensorDataRepository;

    @Override
    @Transactional
    public boolean saveSensorDataAndLatestUpdate(List<SensorDataRequest> requests) {
        List<SensorDataEntity> entities = requests.stream()
                .map(r -> r.toSensorData(DEVICE_ID))
                .toList();

        sensorDataRepository.saveAll(entities);

        SensorDataEntity latestData = entities.stream()
                .max(Comparator.comparing(SensorDataEntity::getRecordedAt))
                .orElseThrow();
        return updateLatest(latestData);
    }

    private boolean updateLatest(SensorDataEntity latestData) {
        var result = sensorLatestRepository.upsert(
                DEVICE_ID,
                latestData.getTemperature(),
                latestData.getHumidity(),
                latestData.getHeatIndex(),
                latestData.getRecordedAt(),
                Instant.now()
        );
        return result > 0;
    }
}
