package com.example.Demotion.Common.Slack.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlackIntegration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // 연동한 Demotion 유저의 ID
    private String teamId; // Slack 팀 ID
    private String accessToken; // OAuth로 받은 토큰
    private String selectedChannelId;
    private LocalDateTime connectedAt;
}
