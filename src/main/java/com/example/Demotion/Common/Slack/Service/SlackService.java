package com.example.Demotion.Common.Slack.Service;

import com.example.Demotion.Common.Slack.Dto.ChannelDto;
import com.example.Demotion.Common.Slack.Entity.SlackIntegration;
import com.example.Demotion.Common.Slack.Repository.SlackIntegrationRepository;
import com.example.Demotion.Common.Slack.Repository.SlackIntegrationRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlackService {

    @Value("${slack.client-id}")
    private String clientId;
    @Value("${slack.client-secret}")
    private String clientSecret;
    @Value("${slack.redirect-uri}")
    private String redirectUri;

    private final SlackIntegrationRepository slackRepo;

    public void exchangeToken(String code, Long demotionUserId) {
        WebClient client = WebClient.create();

        String response = client.post()
                .uri("https://slack.com/api/oauth.v2.access")
                .body(BodyInserters.fromFormData("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("code", code)
                        .with("redirect_uri", redirectUri))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JSONObject json = new JSONObject(response);
        if (!json.getBoolean("ok")) throw new RuntimeException("Slack 인증 실패: " + json);

        String accessToken = json.getString("access_token");
        String teamId = json.getJSONObject("team").getString("id");

        slackRepo.save(SlackIntegration.builder()
                .userId(demotionUserId)
                .accessToken(accessToken)
                .teamId(teamId)
                .connectedAt(LocalDateTime.now())
                .build());
    }

    public List<ChannelDto> getChannels(String accessToken) {
        WebClient client = WebClient.create();

        String result = client.get()
                .uri("https://slack.com/api/conversations.list")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JSONArray channels = new JSONObject(result).getJSONArray("channels");
        List<ChannelDto> list = new ArrayList<>();
        for (int i = 0; i < channels.length(); i++) {
            JSONObject ch = channels.getJSONObject(i);
            list.add(new ChannelDto(ch.getString("id"), ch.getString("name")));
        }
        return list;
    }

    public void saveSelectedChannel(Long demotionUserId, String channelId) {
        SlackIntegration integration = slackRepo.findByUserId(demotionUserId)
                .orElseThrow(() -> new RuntimeException("Slack 연동 정보 없음"));
        integration.setSelectedChannelId(channelId);
        slackRepo.save(integration);
    }

    public void sendMessage(Long demotionUserId, String message) {
        SlackIntegration integration = slackRepo.findByUserId(demotionUserId)
                .orElseThrow(() -> new RuntimeException("Slack 연동 정보 없음"));

        WebClient.create()
                .post()
                .uri("https://slack.com/api/chat.postMessage")
                .header("Authorization", "Bearer " + integration.getAccessToken())
                .bodyValue(new JSONObject()
                        .put("channel", integration.getSelectedChannelId())
                        .put("text", message)
                        .toString())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String getAccessTokenByUserId(Long userId) {
        return slackRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Slack 연동 정보 없음"))
                .getAccessToken();
    }

}
