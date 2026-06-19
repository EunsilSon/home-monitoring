package com.eunsilson.homemonitoring.controller;

import com.eunsilson.homemonitoring.domain.dto.DeviceStatusResponse;
import com.eunsilson.homemonitoring.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    @PostMapping("/heartbeat")
    public ResponseEntity<Void> heartbeat() {
        deviceService.recordHeartbeat();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status")
    public ResponseEntity<DeviceStatusResponse> getStatus() {
        return ResponseEntity.ok(deviceService.getStatus());
    }
}
