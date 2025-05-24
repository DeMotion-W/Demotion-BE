package com.example.Demotion.Domain.Auth.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailVerificationResponseDto {
    private String message;
    private String resetToken;
}
