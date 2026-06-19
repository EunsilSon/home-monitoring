package com.eunsilson.homemonitoring.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "device")
public class DeviceEntity {
    @Id
    private UUID id;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastSeenAt;

    public DeviceEntity(UUID id, String name, String description, Instant createdAt, Instant updatedAt, Instant lastSeenAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastSeenAt = lastSeenAt;
    }

    public static DeviceEntity createDefault(UUID id, Instant now) {
        return new DeviceEntity(
                id,
                "home-monitoring-sensor",
                "DHT22 temperature and humidity collector",
                now,
                now,
                now
        );
    }

    public void markSeen(Instant seenAt) {
        if (createdAt == null) {
            createdAt = seenAt;
        }
        updatedAt = seenAt;
        lastSeenAt = seenAt;
    }
}
