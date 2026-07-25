package com.eunsilson.homemonitoring.slack;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "slack")
public record SlackProperties(
        String webhookUrl
) {
}
