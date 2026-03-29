package com.eunsilson.homemonitoring.controller;

import com.eunsilson.homemonitoring.domain.dto.SensorDataRequest;
import com.eunsilson.homemonitoring.domain.entity.SensorLatestEntity;
import com.eunsilson.homemonitoring.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sensor")
@RequiredArgsConstructor
public class SensorController {
    private final SensorService sensorService;

    @PostMapping("/bulk")
    public ResponseEntity<Void> save(@RequestBody List<SensorDataRequest> requests) {
        if (sensorService.saveSensorDataAndLatestUpdate(requests)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/latest")
    public ResponseEntity<SensorLatestEntity> getLatest() {
        return ResponseEntity.ok().body(sensorService.getLatest());
    }
}
