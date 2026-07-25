package com.eunsilson.homemonitoring;

import com.eunsilson.homemonitoring.domain.DeviceIds;
import com.eunsilson.homemonitoring.domain.dto.ThresholdRequest;
import com.eunsilson.homemonitoring.domain.dto.ThresholdResponse;
import com.eunsilson.homemonitoring.domain.dto.ThresholdResult;
import com.eunsilson.homemonitoring.domain.entity.SensorDataEntity;
import com.eunsilson.homemonitoring.domain.entity.ThresholdEntity;
import com.eunsilson.homemonitoring.repository.ThresholdRepository;
import com.eunsilson.homemonitoring.service.ThresholdServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ThresholdServiceImplTest {

    @Mock
    private ThresholdRepository thresholdRepository;

    @InjectMocks
    private ThresholdServiceImpl thresholdService;

    @Test
    void getThreshold_shouldReturnThresholdWhenExists() {
        UUID deviceId = DeviceIds.DEFAULT_DEVICE_ID;
        ThresholdEntity entity = createTestThreshold(deviceId);
        when(thresholdRepository.findById(deviceId)).thenReturn(Optional.of(entity));

        ThresholdResponse response = thresholdService.getThreshold(deviceId);

        assertNotNull(response);
        assertEquals(deviceId, response.deviceId());
        assertEquals(new BigDecimal("15.00"), response.temperatureMin());
        assertEquals(new BigDecimal("30.00"), response.temperatureMax());
    }

    @Test
    void getThreshold_shouldReturnNullWhenNotExists() {
        UUID deviceId = DeviceIds.DEFAULT_DEVICE_ID;
        when(thresholdRepository.findById(deviceId)).thenReturn(Optional.empty());

        ThresholdResponse response = thresholdService.getThreshold(deviceId);

        assertNull(response);
    }

    @Test
    void saveThreshold_shouldCreateNewThreshold() {
        UUID deviceId = DeviceIds.DEFAULT_DEVICE_ID;
        ThresholdRequest request = new ThresholdRequest(
                new BigDecimal("15"), new BigDecimal("30"),
                new BigDecimal("30"), new BigDecimal("80"),
                new BigDecimal("18"), new BigDecimal("32"),
                true
        );

        when(thresholdRepository.findById(deviceId)).thenReturn(Optional.empty());
        when(thresholdRepository.save(any(ThresholdEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ThresholdResponse response = thresholdService.saveThreshold(deviceId, request);

        assertNotNull(response);
        assertEquals(deviceId, response.deviceId());
        verify(thresholdRepository).save(any(ThresholdEntity.class));
    }

    @Test
    void saveThreshold_shouldUpdateExistingThreshold() {
        UUID deviceId = DeviceIds.DEFAULT_DEVICE_ID;
        ThresholdEntity existing = createTestThreshold(deviceId);
        ThresholdRequest request = new ThresholdRequest(
                new BigDecimal("10"), new BigDecimal("25"),
                new BigDecimal("40"), new BigDecimal("70"),
                new BigDecimal("15"), new BigDecimal("28"),
                true
        );

        when(thresholdRepository.findById(deviceId)).thenReturn(Optional.of(existing));
        when(thresholdRepository.save(any(ThresholdEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ThresholdResponse response = thresholdService.saveThreshold(deviceId, request);

        assertNotNull(response);
        assertEquals(deviceId, response.deviceId());
        verify(thresholdRepository).save(any(ThresholdEntity.class));
    }

    @Test
    void evaluateThreshold_shouldReturnNotExceededWhenNoThreshold() {
        UUID deviceId = DeviceIds.DEFAULT_DEVICE_ID;
        SensorDataEntity sensorData = new SensorDataEntity(
                deviceId, 25.0f, 50.0f, 26.0f, Instant.now()
        );

        when(thresholdRepository.findById(deviceId)).thenReturn(Optional.empty());

        ThresholdResult result = thresholdService.evaluateThreshold(sensorData);

        assertFalse(result.exceeded());
        assertTrue(result.exceededItems().isEmpty());
    }

    @Test
    void evaluateThreshold_shouldDetectTemperatureExceeded() {
        UUID deviceId = DeviceIds.DEFAULT_DEVICE_ID;
        ThresholdEntity threshold = createTestThreshold(deviceId);

        SensorDataEntity sensorData = new SensorDataEntity(
                deviceId, 35.0f, 50.0f, 28.0f, Instant.now()
        );

        when(thresholdRepository.findById(deviceId)).thenReturn(Optional.of(threshold));

        ThresholdResult result = thresholdService.evaluateThreshold(sensorData);

        assertTrue(result.exceeded());
        assertTrue(result.exceededItems().stream().anyMatch(item -> item.contains("Temperature")));
    }

    @Test
    void evaluateThreshold_shouldDetectMultipleExceeded() {
        UUID deviceId = DeviceIds.DEFAULT_DEVICE_ID;
        ThresholdEntity threshold = createTestThreshold(deviceId);

        SensorDataEntity sensorData = new SensorDataEntity(
                deviceId, 35.0f, 90.0f, 40.0f, Instant.now()
        );

        when(thresholdRepository.findById(deviceId)).thenReturn(Optional.of(threshold));

        ThresholdResult result = thresholdService.evaluateThreshold(sensorData);

        assertTrue(result.exceeded());
        assertTrue(result.exceededItems().size() >= 2);
    }

    @Test
    void evaluateThreshold_shouldReturnNotExceededWhenWithinRange() {
        UUID deviceId = DeviceIds.DEFAULT_DEVICE_ID;
        ThresholdEntity threshold = createTestThreshold(deviceId);

        SensorDataEntity sensorData = new SensorDataEntity(
                deviceId, 22.0f, 50.0f, 24.0f, Instant.now()
        );

        when(thresholdRepository.findById(deviceId)).thenReturn(Optional.of(threshold));

        ThresholdResult result = thresholdService.evaluateThreshold(sensorData);

        assertFalse(result.exceeded());
        assertTrue(result.exceededItems().isEmpty());
    }

    @Test
    void evaluateThreshold_shouldDetectBelowMinimum() {
        UUID deviceId = DeviceIds.DEFAULT_DEVICE_ID;
        ThresholdEntity threshold = createTestThreshold(deviceId);

        SensorDataEntity sensorData = new SensorDataEntity(
                deviceId, 10.0f, 50.0f, 24.0f, Instant.now()
        );

        when(thresholdRepository.findById(deviceId)).thenReturn(Optional.of(threshold));

        ThresholdResult result = thresholdService.evaluateThreshold(sensorData);

        assertTrue(result.exceeded());
        assertTrue(result.exceededItems().stream().anyMatch(item -> item.contains("최소")));
    }

    @Test
    void shouldSendNotification_shouldReturnTrueOnFirstExceeded() {
        UUID deviceId = DeviceIds.DEFAULT_DEVICE_ID;

        boolean result = thresholdService.shouldSendNotification(deviceId, true);

        assertTrue(result);
    }

    @Test
    void shouldSendNotification_shouldReturnFalseOnConsecutiveExceeded() {
        UUID deviceId = DeviceIds.DEFAULT_DEVICE_ID;

        thresholdService.shouldSendNotification(deviceId, true);
        boolean result = thresholdService.shouldSendNotification(deviceId, true);

        assertFalse(result);
    }

    @Test
    void shouldSendNotification_shouldReturnTrueAfterNormalRecovery() {
        UUID deviceId = DeviceIds.DEFAULT_DEVICE_ID;

        thresholdService.shouldSendNotification(deviceId, true);
        thresholdService.shouldSendNotification(deviceId, false);
        boolean result = thresholdService.shouldSendNotification(deviceId, true);

        assertTrue(result);
    }

    @Test
    void shouldSendNotification_shouldReturnFalseWhenNotExceeded() {
        UUID deviceId = DeviceIds.DEFAULT_DEVICE_ID;

        boolean result = thresholdService.shouldSendNotification(deviceId, false);

        assertFalse(result);
    }

    private ThresholdEntity createTestThreshold(UUID deviceId) {
        return new ThresholdEntity(
                deviceId,
                new BigDecimal("15.00"),
                new BigDecimal("30.00"),
                new BigDecimal("30.00"),
                new BigDecimal("80.00"),
                new BigDecimal("18.00"),
                new BigDecimal("32.00"),
                true,
                Instant.now(),
                Instant.now()
        );
    }
}
