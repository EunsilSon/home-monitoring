package com.eunsilson.homemonitoring.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "threshold")
public class ThresholdEntity {
    @Id
    private UUID deviceId;

    @Column(precision = 5, scale = 2)
    private BigDecimal temperatureMin;

    @Column(precision = 5, scale = 2)
    private BigDecimal temperatureMax;

    @Column(precision = 5, scale = 2)
    private BigDecimal humidityMin;

    @Column(precision = 5, scale = 2)
    private BigDecimal humidityMax;

    @Column(precision = 5, scale = 2)
    private BigDecimal heatIndexMin;

    @Column(precision = 5, scale = 2)
    private BigDecimal heatIndexMax;

    private Boolean slackEnabled;

    private Instant createdAt;
    private Instant updatedAt;

    public ThresholdEntity(UUID deviceId, BigDecimal temperatureMin, BigDecimal temperatureMax,
                           BigDecimal humidityMin, BigDecimal humidityMax,
                           BigDecimal heatIndexMin, BigDecimal heatIndexMax,
                           Boolean slackEnabled,
                           Instant createdAt, Instant updatedAt) {
        this.deviceId = deviceId;
        this.temperatureMin = temperatureMin;
        this.temperatureMax = temperatureMax;
        this.humidityMin = humidityMin;
        this.humidityMax = humidityMax;
        this.heatIndexMin = heatIndexMin;
        this.heatIndexMax = heatIndexMax;
        this.slackEnabled = slackEnabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void update(BigDecimal temperatureMin, BigDecimal temperatureMax,
                       BigDecimal humidityMin, BigDecimal humidityMax,
                       BigDecimal heatIndexMin, BigDecimal heatIndexMax,
                       Boolean slackEnabled,
                       Instant updatedAt) {
        this.temperatureMin = temperatureMin;
        this.temperatureMax = temperatureMax;
        this.humidityMin = humidityMin;
        this.humidityMax = humidityMax;
        this.heatIndexMin = heatIndexMin;
        this.heatIndexMax = heatIndexMax;
        this.slackEnabled = slackEnabled;
        this.updatedAt = updatedAt;
    }
}
