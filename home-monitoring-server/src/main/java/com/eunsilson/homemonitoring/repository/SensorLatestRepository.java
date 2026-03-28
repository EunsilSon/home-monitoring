package com.eunsilson.homemonitoring.repository;

import com.eunsilson.homemonitoring.domain.entity.SensorLatestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SensorLatestRepository extends JpaRepository<SensorLatestEntity, UUID> {
}
