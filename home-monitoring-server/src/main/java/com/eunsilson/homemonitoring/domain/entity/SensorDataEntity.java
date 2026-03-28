package com.eunsilson.homemonitoring.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Table(name = "sensor_data")
public class SensorDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public UUID deviceId;
    public Float temperature;
    public Float humidity;
    public Float heatIndex;
    public Instant recordedAt;

    public SensorDataEntity(UUID deviceId, Float temperature, Float humidity, Float heatIndex, Instant recordedAt) {
        this.deviceId = deviceId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.heatIndex = heatIndex;
        this.recordedAt = recordedAt;
    }
}
