package com.eunsilson.homemonitoring.controller;

import com.eunsilson.homemonitoring.domain.dto.DeviceStatusResponse;
import com.eunsilson.homemonitoring.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    @GetMapping("/{deviceId}/status")
    public ResponseEntity<DeviceStatusResponse> getStatus(@PathVariable UUID deviceId) {
        return ResponseEntity.ok(deviceService.getStatus(deviceId));
    }
}
