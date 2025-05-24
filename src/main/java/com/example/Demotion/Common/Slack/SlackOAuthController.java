package com.example.Demotion.Common.Slack;

import com.example.Demotion.Common.Slack.Dto.ChannelDto;
import com.example.Demotion.Common.Slack.Dto.SlackChannelRequest;
import com.example.Demotion.Common.Slack.Service.SlackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/slack")
@RequiredArgsConstructor
public class SlackOAuthController {

    private final SlackService slackService;

    @GetMapping("/oauth/callback")
    public String oauthCallback(@RequestParam String code, @RequestParam(required = false) String state) {
        System.out.println("✅ code: " + code);
        System.out.println("✅ state: " + state);

        Long testUserId = 1L; // 테스트용 유저 ID
        slackService.exchangeToken(code, testUserId);

        return "Slack 연동 완료!";
    }


    @GetMapping("/channels")
    public List<ChannelDto> getChannels(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        String token = slackService.getAccessTokenByUserId(userId);
        return slackService.getChannels(token);
    }


    @PostMapping("/channel")
    public void saveChannel(@RequestBody SlackChannelRequest req, Principal principal) {
        slackService.saveSelectedChannel(Long.parseLong(principal.getName()), req.getChannelId());
    }

}
