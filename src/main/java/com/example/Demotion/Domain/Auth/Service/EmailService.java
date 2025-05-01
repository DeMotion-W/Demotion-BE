package com.example.Demotion.Domain.Auth.Service;

import com.example.Demotion.Domain.Auth.Entity.EmailVerificationCode;
import com.example.Demotion.Domain.Auth.Entity.User;
import com.example.Demotion.Domain.Auth.Repository.EmailVerificationCodeRepository;
import com.example.Demotion.Domain.Auth.Repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationCodeRepository codeRepository;
    private final UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String senderEmail;

    // 랜덤 인증코드 생성 (6자리)
    public String createCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // 인증 메일 전송
    public void sendEmail(String toEmail, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

        helper.setTo(toEmail);
        helper.setFrom(senderEmail);
        helper.setSubject("Demotion 인증번호 안내");
        helper.setText(
                "<h1>Demotion 인증번호</h1>" +
                        "<p>아래의 인증번호를 입력해주세요.</p>" +
                        "<strong>" + code + "</strong>",
                true
        );

        mailSender.send(message);
    }

    // 인증 메일 전송 및 db 저장
    public void generateAndSendCode(String email) throws MessagingException {
        String code = createCode();

        // 이메일 전송
        sendEmail(email, code);

        // DB에 저장
        EmailVerificationCode verification = EmailVerificationCode.builder()
                .email(email)
                .code(code)
                .createdAt(LocalDateTime.now())
                .build();

        codeRepository.deleteAllByEmail(email);
        codeRepository.save(verification);
    }

    // 이메일 인증번호 확인
    public void verifyCodeAndActivate(String email, String code) {
        EmailVerificationCode verification = codeRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new IllegalArgumentException("인증번호를 찾을 수 없습니다."));

        if (!verification.getCode().equals(code)) {
            throw new IllegalArgumentException("인증번호가 올바르지 않습니다.");
        }

        if (verification.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new IllegalArgumentException("인증번호가 만료되었습니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setEmailVerified(true);
        userRepository.save(user);
    }


}
