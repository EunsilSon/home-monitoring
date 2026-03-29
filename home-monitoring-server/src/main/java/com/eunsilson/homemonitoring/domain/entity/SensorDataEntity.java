package com.eunsilson.homemonitoring.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@Data
@Entity
@Table(name = "sensor_data")
public class SensorDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID deviceId;
    private float temperature;
    private float humidity;
    private float heatIndex;
    private Instant recordedAt;

    public SensorDataEntity(UUID deviceId, float temperature, float humidity, float heatIndex, Instant recordedAt) {
        this.deviceId = deviceId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.heatIndex = heatIndex;
        this.recordedAt = recordedAt;
    }
}
