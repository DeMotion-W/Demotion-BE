package com.example.Demotion.Domain.Auth.Controller;

import com.example.Demotion.Domain.Auth.Repository.EmailVerificationCodeRepository;
import com.example.Demotion.Domain.Auth.Service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class EmailController {

    private final EmailService emailService;
    private final EmailVerificationCodeRepository emailVerificationCodeRepository;

    @PostMapping("/email/send-code")
    public ResponseEntity<String> sendCode(@RequestParam String email) {
        try {
            emailService.generateAndSendCode(email);
            return ResponseEntity.ok("인증번호 전송 완료");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("이메일 전송 실패");
        }
    }

    @PostMapping("/email/code-verify")
    public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String code) {
        return emailVerificationCodeRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .map(verification -> {
                    if (!verification.getCode().equals(code)) {
                        return ResponseEntity.badRequest().body("인증번호가 올바르지 않습니다.");
                    }
                    if (verification.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
                        return ResponseEntity.badRequest().body("인증번호가 만료되었습니다.");
                    }
                    return ResponseEntity.ok("인증 성공");
                })
                .orElse(ResponseEntity.badRequest().body("인증번호를 찾을 수 없습니다."));
    }

}
