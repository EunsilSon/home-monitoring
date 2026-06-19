package com.eunsilson.homemonitoring.domain.dto;

import java.time.Instant;
import java.util.UUID;

public record DeviceStatusResponse(
        UUID deviceId,
        String status,
        Instant lastSeenAt,
        Instant serverTime,
        long offlineThresholdSeconds
) {
}
