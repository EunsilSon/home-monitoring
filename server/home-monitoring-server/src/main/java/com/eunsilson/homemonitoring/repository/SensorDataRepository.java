package com.eunsilson.homemonitoring.repository;

import com.eunsilson.homemonitoring.domain.entity.SensorDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorDataRepository extends JpaRepository<SensorDataEntity, Long> {
}
