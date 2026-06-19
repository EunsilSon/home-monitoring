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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceServiceImplTest {
    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceServiceImpl deviceService;

    @Test
    void recordHeartbeat_shouldCreateDefaultDeviceWhenMissing() {
        when(deviceRepository.findById(DeviceIds.DEFAULT_DEVICE_ID)).thenReturn(Optional.empty());

        deviceService.recordHeartbeat();

        ArgumentCaptor<DeviceEntity> captor = ArgumentCaptor.forClass(DeviceEntity.class);
        verify(deviceRepository).save(captor.capture());

        DeviceEntity saved = captor.getValue();
        assertEquals(DeviceIds.DEFAULT_DEVICE_ID, saved.getId());
        assertNotNull(saved.getLastSeenAt());
    }

    @Test
    void getStatus_shouldReturnOnlineWhenLastSeenIsRecent() {
        Instant now = Instant.now();
        DeviceEntity device = new DeviceEntity(
                DeviceIds.DEFAULT_DEVICE_ID,
                "home-monitoring-sensor",
                "DHT22 temperature and humidity collector",
                now,
                now,
                now
        );
        when(deviceRepository.findById(DeviceIds.DEFAULT_DEVICE_ID)).thenReturn(Optional.of(device));

        DeviceStatusResponse result = deviceService.getStatus();

        assertEquals(DeviceIds.DEFAULT_DEVICE_ID, result.deviceId());
        assertEquals("ONLINE", result.status());
        assertEquals(now, result.lastSeenAt());
    }
}
