package com.eunsilson.homemonitoring.service;

import com.eunsilson.homemonitoring.domain.dto.SensorDataRequest;
import com.eunsilson.homemonitoring.domain.entity.SensorLatestEntity;

import java.util.List;
import java.util.UUID;

public interface SensorService {
    SensorLatestEntity getLatest(UUID deviceId);
    boolean saveSensorDataAndLatestUpdate(List<SensorDataRequest> requests);
}
