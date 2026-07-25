package com.eunsilson.homemonitoring.repository;

import com.eunsilson.homemonitoring.domain.entity.ThresholdEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ThresholdRepository extends JpaRepository<ThresholdEntity, UUID> {
}
