package com.eunsilson.homemonitoring.controller;

import com.eunsilson.homemonitoring.domain.dto.ThresholdRequest;
import com.eunsilson.homemonitoring.domain.dto.ThresholdResponse;
import com.eunsilson.homemonitoring.service.ThresholdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/threshold")
@RequiredArgsConstructor
public class ThresholdController {

    private final ThresholdService thresholdService;

    @GetMapping("/{deviceId}")
    public ResponseEntity<ThresholdResponse> getThreshold(@PathVariable UUID deviceId) {
        ThresholdResponse response = thresholdService.getThreshold(deviceId);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{deviceId}")
    public ResponseEntity<ThresholdResponse> saveThreshold(
            @PathVariable UUID deviceId,
            @RequestBody ThresholdRequest request) {
        ThresholdResponse response = thresholdService.saveThreshold(deviceId, request);
        return ResponseEntity.ok(response);
    }
}
