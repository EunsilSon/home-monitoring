package com.eunsilson.homemonitoring.domain.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public record ThresholdResult(
        boolean exceeded,
        List<String> exceededItems
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<String> items = new ArrayList<>();

        public Builder check(String label, float value, BigDecimal min, BigDecimal max) {
            if (min != null && BigDecimal.valueOf(value).compareTo(min) <= 0) {
                items.add(String.format("%s: %.1f (최소 %.1f 미만)", label, value, min));
            }
            if (max != null && BigDecimal.valueOf(value).compareTo(max) >= 0) {
                items.add(String.format("%s: %.1f (최대 %.1f 초과)", label, value, max));
            }
            return this;
        }

        public ThresholdResult build() {
            return new ThresholdResult(!items.isEmpty(), List.copyOf(items));
        }
    }
}
