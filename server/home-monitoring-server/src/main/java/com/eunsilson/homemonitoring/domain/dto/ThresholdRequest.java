package com.eunsilson.homemonitoring.domain.dto;

import java.math.BigDecimal;

public record ThresholdRequest(
        BigDecimal temperatureMin,
        BigDecimal temperatureMax,
        BigDecimal humidityMin,
        BigDecimal humidityMax,
        BigDecimal heatIndexMin,
        BigDecimal heatIndexMax,
        Boolean slackEnabled
) {
}
