package com.eunsilson.homemonitoring.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "sensor_latest")
@NoArgsConstructor
@AllArgsConstructor
public class SensorLatestEntity {
    @Id
    private UUID deviceId;
    private Float temperature;
    private Float humidity;
    private Float heatIndex;
    private Instant recordedAt;
    private Instant updatedAt;
}