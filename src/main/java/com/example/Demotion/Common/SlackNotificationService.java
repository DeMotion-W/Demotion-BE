package com.example.Demotion.Common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SlackNotificationService {

    @Value("${SLACKURL}")
    private String webhookUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendSlackMessage(String message) {
        Map<String, String> payload = new HashMap<>();
        payload.put("text", message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

        restTemplate.postForEntity(webhookUrl, entity, String.class);
    }
}
