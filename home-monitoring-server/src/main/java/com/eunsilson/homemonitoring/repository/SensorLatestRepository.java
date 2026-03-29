package com.eunsilson.homemonitoring.repository;

import com.eunsilson.homemonitoring.domain.entity.SensorLatestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.UUID;

public interface SensorLatestRepository extends JpaRepository<SensorLatestEntity, UUID> {
    @Modifying
    @Query(value = """
            INSERT INTO sensor_latest (device_id, temperature, humidity, heat_index, recorded_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (device_id)
            DO UPDATE SET
            temperature = EXCLUDED.temperature,
            humidity = EXCLUDED.humidity,
            heat_index = EXCLUDED.heat_index,
            recorded_at = EXCLUDED.recorded_at,
            updated_at = EXCLUDED.updated_at
            """, nativeQuery = true)
    int upsert(UUID deviceId, float temperature, float humidity, float heatIndex, Instant recorded_at, Instant updated_at);
    SensorLatestEntity findByDeviceId(UUID deviceId);
}
