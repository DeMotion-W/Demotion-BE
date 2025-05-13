package com.example.Demotion.Domain.Auth.Controller;

import com.example.Demotion.Common.ErrorCode;
import com.example.Demotion.Common.ErrorDomain;
import com.example.Demotion.Domain.Auth.Dto.EmailRequestDto;
import com.example.Demotion.Domain.Auth.Dto.EmailVerificationRequestDto;
import com.example.Demotion.Domain.Auth.Dto.EmailVerificationResponseDto;
import com.example.Demotion.Domain.Auth.Repository.EmailVerificationCodeRepository;
import com.example.Demotion.Domain.Auth.Service.AuthService;
import com.example.Demotion.Domain.Auth.Service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/verify-email")
public class EmailController {

    private final EmailService emailService;
    private final AuthService authService;

    // 인증번호 전송
    @PostMapping("/request")
    public ResponseEntity<?> sendCode(@RequestBody EmailRequestDto request) {
        try {
            emailService.generateAndSendCode(request.getEmail());
            return ResponseEntity.ok(Map.of("message", "인증 코드가 이메일로 전송되었습니다."));
        } catch (MessagingException e) {
            throw new ErrorDomain(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 이메일 인증번호 확인
    @PostMapping("/confirm")
    public ResponseEntity<?> verifyCode(@RequestBody EmailVerificationRequestDto request) {
        emailService.verifyCodeAndActivate(request.getEmail(), request.getVerificationCode());

        String resetToken = UUID.randomUUID().toString();

        return ResponseEntity.ok(
                Map.of(
                        "message", "인증이 완료되었습니다.",
                        "resetToken", resetToken
                )
        );
    }
}
