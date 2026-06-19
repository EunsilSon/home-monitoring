package com.eunsilson.homemonitoring;

import com.eunsilson.homemonitoring.domain.DeviceIds;
import com.eunsilson.homemonitoring.domain.dto.SensorDataRequest;
import com.eunsilson.homemonitoring.domain.entity.SensorLatestEntity;
import com.eunsilson.homemonitoring.repository.SensorDataRepository;
import com.eunsilson.homemonitoring.repository.SensorLatestRepository;
import com.eunsilson.homemonitoring.service.DeviceService;
import com.eunsilson.homemonitoring.service.SensorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SensorServiceImplTest {
    @Mock
    private SensorDataRepository sensorDataRepository;

    @Mock
    private SensorLatestRepository sensorLatestRepository;

    @Mock
    private DeviceService deviceService;

    @InjectMocks
    private SensorServiceImpl sensorService;

    @Test
    void saveSensorDataAndLatestUpdate_shouldSaveAllAndUpdateLatest() {

        List<SensorDataRequest> requests = IntStream.range(0, 30)
                .mapToObj(i -> new SensorDataRequest(
                        "20.0",
                        "30.0",
                        "21.0",
                        "2026-03-29T10:" + String.format("%02d", i) + ":00Z"
                ))
                .toList();

        when(sensorLatestRepository.upsert(
                eq(DeviceIds.DEFAULT_DEVICE_ID),
                eq(20.0f),
                eq(30.0f),
                eq(21.0f),
                eq(Instant.parse("2026-03-29T10:29:00Z")),
                any(Instant.class)
        )).thenReturn(1);

        boolean result = sensorService.saveSensorDataAndLatestUpdate(requests);

        assertTrue(result);
        verify(sensorDataRepository).saveAll(anyList());
        verify(deviceService).recordHeartbeat();
    }

    @Test
    void getLatest_shouldReturnLatestSensorData() {

        SensorLatestEntity expected = new SensorLatestEntity(
                DeviceIds.DEFAULT_DEVICE_ID,
                20.0f,
                30.0f,
                21.0f,
                Instant.parse("2026-03-29T11:00:00Z"),
                Instant.now()
        );

        when(sensorLatestRepository.findByDeviceId(any(UUID.class)))
                .thenReturn(expected);

        SensorLatestEntity result = sensorService.getLatest();

        verify(sensorLatestRepository, times(1))
                .findByDeviceId(any(UUID.class));

        assertEquals(expected, result);
    }
}
