package com.example.Demotion.Domain.Auth.Controller;

import com.example.Demotion.Domain.Auth.Repository.EmailVerificationCodeRepository;
import com.example.Demotion.Domain.Auth.Service.AuthService;
import com.example.Demotion.Domain.Auth.Service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/verify-email")
public class EmailController {

    private final EmailService emailService;
    private final AuthService authService;

    // 이메일 인증번호 전송
    @PostMapping("/request")
    public ResponseEntity<String> sendCode(@RequestParam String email) {
        try {
            emailService.generateAndSendCode(email);
            return ResponseEntity.ok("인증 코드가 이메일로 전송되었습니다.");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("실패?");
        }
    }

    // 이메일 인증번호 확인
    @PostMapping("/confirm")
    public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String code) {
        try {
            emailService.verifyCodeAndActivate(email, code);
            return ResponseEntity.ok("이메일 인증 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
