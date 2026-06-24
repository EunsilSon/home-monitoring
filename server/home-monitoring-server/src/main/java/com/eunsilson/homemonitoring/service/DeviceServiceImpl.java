package com.eunsilson.homemonitoring.service;

import com.eunsilson.homemonitoring.domain.dto.DeviceStatusResponse;
import com.eunsilson.homemonitoring.domain.entity.DeviceEntity;
import com.eunsilson.homemonitoring.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    private static final Duration OFFLINE_THRESHOLD = Duration.ofSeconds(180);

    private final DeviceRepository deviceRepository;

    @Override
    @Transactional
    public void recordSensorDataReceived(UUID deviceId) {
        Instant now = Instant.now();
        DeviceEntity device = deviceRepository.findById(deviceId)
                .orElseGet(() -> DeviceEntity.createDefault(deviceId, now));

        device.markSeen(now);
        deviceRepository.save(device);
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceStatusResponse getStatus(UUID deviceId) {
        Instant now = Instant.now();
        Instant lastSeenAt = deviceRepository.findById(deviceId)
                .map(DeviceEntity::getLastSeenAt)
                .orElse(null);

        String status = resolveStatus(lastSeenAt, now);

        return new DeviceStatusResponse(
                deviceId,
                status,
                lastSeenAt,
                now,
                OFFLINE_THRESHOLD.toSeconds()
        );
    }

    private String resolveStatus(Instant lastSeenAt, Instant now) {
        if (lastSeenAt == null) {
            return "UNKNOWN";
        }
        if (lastSeenAt.isBefore(now.minus(OFFLINE_THRESHOLD))) {
            return "OFF";
        }
        return "ON";
    }
}
