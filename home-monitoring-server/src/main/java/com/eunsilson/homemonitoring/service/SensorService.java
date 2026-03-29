package com.eunsilson.homemonitoring.service;

import com.eunsilson.homemonitoring.domain.dto.SensorDataRequest;
import com.eunsilson.homemonitoring.domain.entity.SensorLatestEntity;

import java.util.List;

public interface SensorService {
    boolean saveSensorDataAndLatestUpdate(List<SensorDataRequest> requests);
}
