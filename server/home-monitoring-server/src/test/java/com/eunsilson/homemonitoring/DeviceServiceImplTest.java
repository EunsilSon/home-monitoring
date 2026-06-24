package com.eunsilson.homemonitoring;

import com.eunsilson.homemonitoring.domain.DeviceIds;
import com.eunsilson.homemonitoring.domain.dto.DeviceStatusResponse;
import com.eunsilson.homemonitoring.domain.entity.DeviceEntity;
import com.eunsilson.homemonitoring.repository.DeviceRepository;
import com.eunsilson.homemonitoring.service.DeviceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceServiceImplTest {
    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceServiceImpl deviceService;

    @Test
    void recordSensorDataReceived_shouldCreateDefaultDeviceWhenMissing() {
        UUID deviceId = DeviceIds.DEFAULT_DEVICE_ID;
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.empty());

        deviceService.recordSensorDataReceived(deviceId);

        ArgumentCaptor<DeviceEntity> captor = ArgumentCaptor.forClass(DeviceEntity.class);
        verify(deviceRepository).save(captor.capture());

        DeviceEntity saved = captor.getValue();
        assertEquals(deviceId, saved.getId());
        assertNotNull(saved.getLastSeenAt());
    }

    @Test
    void getStatus_shouldReturnOnlineWhenLastSeenIsRecent() {
        Instant now = Instant.now();
        UUID deviceId = DeviceIds.DEFAULT_DEVICE_ID;
        DeviceEntity device = new DeviceEntity(
                deviceId,
                "home-monitoring-sensor",
                "DHT22 temperature and humidity collector",
                now,
                now,
                now
        );
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(device));

        DeviceStatusResponse result = deviceService.getStatus(deviceId);

        assertEquals(deviceId, result.deviceId());
        assertEquals("ON", result.status());
        assertEquals(now, result.lastSeenAt());
    }

    @Test
    void getStatus_shouldReturnOffWhenLastSeenIsOlderThanThreshold() {
        Instant lastSeenAt = Instant.now().minusSeconds(240);
        UUID deviceId = DeviceIds.DEFAULT_DEVICE_ID;
        DeviceEntity device = new DeviceEntity(
                deviceId,
                "home-monitoring-sensor",
                "DHT22 temperature and humidity collector",
                lastSeenAt,
                lastSeenAt,
                lastSeenAt
        );
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(device));

        DeviceStatusResponse result = deviceService.getStatus(deviceId);

        assertEquals(deviceId, result.deviceId());
        assertEquals("OFF", result.status());
        assertEquals(lastSeenAt, result.lastSeenAt());
    }

    @Test
    void getStatus_shouldReturnUnknownWhenDeviceHasNeverReceivedSensorData() {
        UUID deviceId = UUID.randomUUID();
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.empty());

        DeviceStatusResponse result = deviceService.getStatus(deviceId);

        assertEquals(deviceId, result.deviceId());
        assertEquals("UNKNOWN", result.status());
        assertNull(result.lastSeenAt());
    }
}
