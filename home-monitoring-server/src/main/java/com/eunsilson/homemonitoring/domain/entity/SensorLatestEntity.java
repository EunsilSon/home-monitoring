package com.eunsilson.homemonitoring.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Table(name = "sensor_latest")
public class SensorLatestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID deviceId;
    public Float temperature;
    public Float humidity;
    public Float heatIndex;
    public Instant updatedAt;
}