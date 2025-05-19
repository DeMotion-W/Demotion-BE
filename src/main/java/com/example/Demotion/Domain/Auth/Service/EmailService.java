package com.example.Demotion.Domain.Auth.Service;

import com.example.Demotion.Common.ErrorCode;
import com.example.Demotion.Common.ErrorDomain;
import com.example.Demotion.Domain.Auth.Entity.EmailVerificationCode;
import com.example.Demotion.Domain.Auth.Repository.EmailVerificationCodeRepository;
import com.example.Demotion.Domain.Auth.Repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationCodeRepository codeRepository;
    private final UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public String createCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public void sendEmail(String toEmail, String username, String code) throws MessagingException {
        String htmlContent = buildEmailBody(username, code);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

        helper.setTo(toEmail);
        helper.setFrom(senderEmail);
        helper.setSubject("Demotion 인증번호 안내");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    public String buildEmailBody(String username, String code) {
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("static/email-verification-template.html");

            if (inputStream == null) {
                throw new ErrorDomain(ErrorCode.INTERNAL_SERVER_ERROR);
            }

            String html = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            html = html.replace("{username}", username);
            html = html.replace("{code}", code);

            return html;
        } catch (IOException e) {
            throw new ErrorDomain(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void generateAndSendCode(String email) throws MessagingException {
        String code = createCode();

        // 임시 username: 이메일 앞부분 (e.g. kimtae from kimtae@example.com)
        String username = email.split("@")[0];

        sendEmail(email, username, code);
        codeRepository.deleteAllByEmail(email);

        EmailVerificationCode verification = EmailVerificationCode.builder()
                .email(email)
                .code(code)
                .createdAt(LocalDateTime.now())
                .build();

        codeRepository.save(verification);
    }

    public void verifyCodeAndActivate(String email, String code) {
        EmailVerificationCode verification = codeRepository
                .findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.INVALID_VERIFICATION_CODE));

        if (!verification.getCode().equals(code)) {
            throw new ErrorDomain(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        if (verification.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new ErrorDomain(ErrorCode.INVALID_VERIFICATION_CODE);
        }
    }
}
