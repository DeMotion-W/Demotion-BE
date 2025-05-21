package com.example.Demotion.Domain.Auth.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessTokenLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String email;

    @Column(length = 2048) // 토큰 길이에 따라 충분히 확보
    private String accessToken;

    private LocalDateTime createdAt;
}
