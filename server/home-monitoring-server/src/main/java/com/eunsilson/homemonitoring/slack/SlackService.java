package com.eunsilson.homemonitoring.slack;

import com.eunsilson.homemonitoring.domain.dto.ThresholdResult;
import com.eunsilson.homemonitoring.domain.entity.SensorDataEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class SlackService {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.of("Asia/Seoul"));

    private final RestClient restClient;
    private final SlackProperties slackProperties;

    public void send(String message) {
        restClient.post()
                .uri(slackProperties.webhookUrl())
                .body(new SlackMessage(message))
                .retrieve()
                .toBodilessEntity();
    }

    public void sendThresholdAlert(SensorDataEntity sensorData, ThresholdResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("[센서 임계값 초과]\n\n");
        sb.append("Device : ").append(sensorData.getDeviceId()).append("\n\n");

        for (String item : result.exceededItems()) {
            sb.append(item).append("\n");
        }

        sb.append("\n측정시간\n");
        String formattedTime = FORMATTER.format(sensorData.getRecordedAt());
        sb.append(formattedTime);

        send(sb.toString());
    }
}
