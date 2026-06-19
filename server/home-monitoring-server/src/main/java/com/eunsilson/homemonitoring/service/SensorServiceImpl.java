package com.eunsilson.homemonitoring.service;

import com.eunsilson.homemonitoring.domain.DeviceIds;
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

@Service
@RequiredArgsConstructor
public class SensorServiceImpl implements SensorService {
    private final SensorDataRepository sensorDataRepository;
    private final SensorLatestRepository sensorLatestRepository;
    private final DeviceService deviceService;

    public SensorLatestEntity getLatest() {
        return sensorLatestRepository.findByDeviceId(DeviceIds.DEFAULT_DEVICE_ID);
    }

    @Override
    @Transactional
    public boolean saveSensorDataAndLatestUpdate(List<SensorDataRequest> requests) {
        List<SensorDataEntity> entities = requests.stream()
                .map(r -> r.toSensorData(DeviceIds.DEFAULT_DEVICE_ID))
                .toList();

        sensorDataRepository.saveAll(entities);

        SensorDataEntity latestData = entities.stream()
                .max(Comparator.comparing(SensorDataEntity::getRecordedAt))
                .orElseThrow();

        boolean updated = updateLatest(latestData);
        if (updated) {
            deviceService.recordHeartbeat();
        }
        return updated;
    }

    private boolean updateLatest(SensorDataEntity latestData) {
        var result = sensorLatestRepository.upsert(
                DeviceIds.DEFAULT_DEVICE_ID,
                latestData.getTemperature(),
                latestData.getHumidity(),
                latestData.getHeatIndex(),
                latestData.getRecordedAt(),
                Instant.now()
        );
        return result > 0;
    }
}
