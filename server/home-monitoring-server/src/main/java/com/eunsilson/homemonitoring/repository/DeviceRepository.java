package com.eunsilson.homemonitoring.repository;

import com.eunsilson.homemonitoring.domain.entity.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeviceRepository extends JpaRepository<DeviceEntity, UUID> {
}
