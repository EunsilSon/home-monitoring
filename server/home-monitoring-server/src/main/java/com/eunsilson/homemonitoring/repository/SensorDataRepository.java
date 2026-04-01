package com.eunsilson.homemonitoring.repository;

import com.eunsilson.homemonitoring.domain.entity.SensorDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SensorDataRepository extends JpaRepository<SensorDataEntity, UUID> {
}
