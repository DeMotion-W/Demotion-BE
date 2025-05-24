package com.example.Demotion.Domain.Auth.Dto;

import lombok.Getter;

@Getter
public class EmailVerificationRequestDto {
    private String email;
    private String verificationCode;
}
