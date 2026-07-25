package com.eunsilson.homemonitoring;

import com.eunsilson.homemonitoring.domain.DeviceIds;
import com.eunsilson.homemonitoring.domain.dto.SensorDataRequest;
import com.eunsilson.homemonitoring.domain.dto.ThresholdResult;
import com.eunsilson.homemonitoring.domain.entity.SensorLatestEntity;
import com.eunsilson.homemonitoring.repository.SensorDataRepository;
import com.eunsilson.homemonitoring.repository.SensorLatestRepository;
import com.eunsilson.homemonitoring.service.DeviceService;
import com.eunsilson.homemonitoring.service.SensorServiceImpl;
import com.eunsilson.homemonitoring.service.ThresholdService;
import com.eunsilson.homemonitoring.slack.SlackService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
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

    @Mock
    private ThresholdService thresholdService;

    @Mock
    private SlackService slackService;

    @InjectMocks
    private SensorServiceImpl sensorService;

    @Test
    void saveSensorDataAndLatestUpdate_shouldSaveAllAndUpdateLatest() {

        List<SensorDataRequest> requests = IntStream.range(0, 30)
                .mapToObj(i -> new SensorDataRequest(
                        DeviceIds.DEFAULT_DEVICE_ID,
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

        ThresholdResult notExceeded = new ThresholdResult(false, List.of());
        when(thresholdService.evaluateThreshold(any())).thenReturn(notExceeded);
        when(thresholdService.shouldSendNotification(any(UUID.class), eq(false))).thenReturn(false);

        boolean result = sensorService.saveSensorDataAndLatestUpdate(requests);

        assertTrue(result);
        verify(deviceService).recordSensorDataReceived(DeviceIds.DEFAULT_DEVICE_ID);
        verify(sensorDataRepository).saveAll(anyList());
        verify(thresholdService, atLeastOnce()).evaluateThreshold(any());
        verify(slackService, never()).sendThresholdAlert(any(), any());
    }

    @Test
    void saveSensorDataAndLatestUpdate_shouldSendSlackAlertWhenThresholdExceeded() {

        List<SensorDataRequest> requests = List.of(
                new SensorDataRequest(
                        DeviceIds.DEFAULT_DEVICE_ID,
                        "35.0",
                        "90.0",
                        "40.0",
                        "2026-03-29T10:00:00Z"
                )
        );

        when(sensorLatestRepository.upsert(
                eq(DeviceIds.DEFAULT_DEVICE_ID),
                eq(35.0f),
                eq(90.0f),
                eq(40.0f),
                eq(Instant.parse("2026-03-29T10:00:00Z")),
                any(Instant.class)
        )).thenReturn(1);

        ThresholdResult exceeded = new ThresholdResult(true,
                List.of("Temperature: 35.0 (최대 30.0 초과)", "Humidity: 90.0 (최대 80.0 초과)"));
        when(thresholdService.evaluateThreshold(any())).thenReturn(exceeded);
        when(thresholdService.shouldSendNotification(eq(DeviceIds.DEFAULT_DEVICE_ID), eq(true)))
                .thenReturn(true);

        boolean result = sensorService.saveSensorDataAndLatestUpdate(requests);

        assertTrue(result);
        verify(deviceService).recordSensorDataReceived(DeviceIds.DEFAULT_DEVICE_ID);
        verify(sensorDataRepository).saveAll(anyList());
        verify(thresholdService).evaluateThreshold(any());
        verify(thresholdService).shouldSendNotification(eq(DeviceIds.DEFAULT_DEVICE_ID), eq(true));
        verify(slackService).sendThresholdAlert(any(), eq(exceeded));
    }

    @Test
    void saveSensorDataAndLatestUpdate_shouldNotSendDuplicateAlert() {

        List<SensorDataRequest> requests = List.of(
                new SensorDataRequest(
                        DeviceIds.DEFAULT_DEVICE_ID,
                        "35.0",
                        "90.0",
                        "40.0",
                        "2026-03-29T10:00:00Z"
                )
        );

        when(sensorLatestRepository.upsert(
                eq(DeviceIds.DEFAULT_DEVICE_ID),
                eq(35.0f),
                eq(90.0f),
                eq(40.0f),
                eq(Instant.parse("2026-03-29T10:00:00Z")),
                any(Instant.class)
        )).thenReturn(1);

        ThresholdResult exceeded = new ThresholdResult(true,
                List.of("Temperature: 35.0 (최대 30.0 초과)"));
        when(thresholdService.evaluateThreshold(any())).thenReturn(exceeded);
        when(thresholdService.shouldSendNotification(eq(DeviceIds.DEFAULT_DEVICE_ID), eq(true)))
                .thenReturn(false);

        boolean result = sensorService.saveSensorDataAndLatestUpdate(requests);

        assertTrue(result);
        verify(thresholdService).evaluateThreshold(any());
        verify(thresholdService).shouldSendNotification(eq(DeviceIds.DEFAULT_DEVICE_ID), eq(true));
        verify(slackService, never()).sendThresholdAlert(any(), any());
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

        SensorLatestEntity result = sensorService.getLatest(DeviceIds.DEFAULT_DEVICE_ID);

        verify(sensorLatestRepository, times(1))
                .findByDeviceId(any(UUID.class));

        assertEquals(expected, result);
    }

    @Test
    void sendSlackAlertIfThresholdExceeded_shouldEvaluateAndNotify() {

        List<SensorDataRequest> requests = List.of(
                new SensorDataRequest(
                        DeviceIds.DEFAULT_DEVICE_ID,
                        "35.0",
                        "90.0",
                        "40.0",
                        "2026-03-29T10:00:00Z"
                )
        );

        ThresholdResult exceeded = new ThresholdResult(true,
                List.of("Temperature: 35.0 (최대 30.0 초과)"));
        when(thresholdService.evaluateThreshold(any())).thenReturn(exceeded);
        when(thresholdService.shouldSendNotification(eq(DeviceIds.DEFAULT_DEVICE_ID), eq(true)))
                .thenReturn(true);

        boolean result = sensorService.sendSlackAlertIfThresholdExceeded(
                DeviceIds.DEFAULT_DEVICE_ID, requests);

        assertTrue(result);
        verify(thresholdService).evaluateThreshold(any());
        verify(slackService).sendThresholdAlert(any(), eq(exceeded));
    }
}
