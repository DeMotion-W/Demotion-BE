package com.example.Demotion.Domain.Auth.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public record LoginResponseDto(String accessToken, Long userId, String name) {}
