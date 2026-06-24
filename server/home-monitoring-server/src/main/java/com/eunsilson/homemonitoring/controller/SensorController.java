package com.eunsilson.homemonitoring.controller;

import com.eunsilson.homemonitoring.domain.dto.SensorDataRequest;
import com.eunsilson.homemonitoring.domain.entity.SensorLatestEntity;
import com.eunsilson.homemonitoring.service.SensorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sensor")
@RequiredArgsConstructor
public class SensorController {
    private final SensorService sensorService;

    @PostMapping("/bulk")
    public ResponseEntity<Void> save(@Valid @RequestBody List<SensorDataRequest> requests) {
        if (sensorService.saveSensorDataAndLatestUpdate(requests)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/{deviceId}/latest")
    public ResponseEntity<SensorLatestEntity> getLatest(@PathVariable UUID deviceId) {
        return ResponseEntity.ok().body(sensorService.getLatest(deviceId));
    }
}
