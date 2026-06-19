package com.eunsilson.homemonitoring.service;

import com.eunsilson.homemonitoring.domain.DeviceIds;
import com.eunsilson.homemonitoring.domain.dto.DeviceStatusResponse;
import com.eunsilson.homemonitoring.domain.entity.DeviceEntity;
import com.eunsilson.homemonitoring.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    private static final Duration OFFLINE_THRESHOLD = Duration.ofSeconds(45);

    private final DeviceRepository deviceRepository;

    @Override
    @Transactional
    public void recordHeartbeat() {
        Instant now = Instant.now();
        DeviceEntity device = deviceRepository.findById(DeviceIds.DEFAULT_DEVICE_ID)
                .orElseGet(() -> DeviceEntity.createDefault(DeviceIds.DEFAULT_DEVICE_ID, now));

        device.markSeen(now);
        deviceRepository.save(device);
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceStatusResponse getStatus() {
        Instant now = Instant.now();
        Instant lastSeenAt = deviceRepository.findById(DeviceIds.DEFAULT_DEVICE_ID)
                .map(DeviceEntity::getLastSeenAt)
                .orElse(null);

        String status = resolveStatus(lastSeenAt, now);

        return new DeviceStatusResponse(
                DeviceIds.DEFAULT_DEVICE_ID,
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
            return "OFFLINE";
        }
        return "ONLINE";
    }
}
