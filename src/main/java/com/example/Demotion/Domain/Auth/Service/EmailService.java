package com.example.Demotion.Domain.Auth.Service;

import com.example.Demotion.Domain.Auth.Entity.EmailVerificationCode;
import com.example.Demotion.Domain.Auth.Repository.EmailVerificationCodeRepository;
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

    @Value("${spring.mail.username}")
    private String senderEmail;

    // 랜덤 인증코드 생성 (6자리)
    public String createCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            key.append(random.nextInt(10)); // 0 ~ 9
        }

        return key.toString();    }

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

        codeRepository.save(verification);
    }
}
