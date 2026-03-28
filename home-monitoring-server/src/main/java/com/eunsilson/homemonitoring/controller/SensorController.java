package com.eunsilson.homemonitoring.controller;

import com.eunsilson.homemonitoring.domain.dto.SensorDataRequest;
import com.eunsilson.homemonitoring.service.SensorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sensor")
@AllArgsConstructor
public class SensorController {
    private final SensorService sensorService;
    private final UUID DEVICE_ID = UUID.fromString("220aa852-ee70-4105-8cf5-98c23cb5e631");

    @PostMapping("/bulk")
    public ResponseEntity<Void> save(@RequestBody List<SensorDataRequest> requests) {
        sensorService.saveAll(DEVICE_ID, requests);
        return ResponseEntity.ok().build();
    }
}
