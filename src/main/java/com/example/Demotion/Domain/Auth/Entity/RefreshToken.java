package com.example.Demotion.Domain.Auth.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email; // 토큰 주인 식별자
    private String expiration; // 만료 시간

    @Column(nullable = false, unique = true)
    private String refresh; // 토큰

    private LocalDateTime createdAt = LocalDateTime.now();

}
