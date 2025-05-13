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

    // 인증번호 전송
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

    // 트랜잭션 적용 필요
    @Transactional
    public void generateAndSendCode(String email) throws MessagingException {
        String code = createCode();

        sendEmail(email, code);

        codeRepository.deleteAllByEmail(email);

        EmailVerificationCode verification = EmailVerificationCode.builder()
                .email(email)
                .code(code)
                .createdAt(LocalDateTime.now())
                .build();

        codeRepository.save(verification);
    }

    // 인증번호 확인
    public void verifyCodeAndActivate(String email, String code) {
        EmailVerificationCode verification = codeRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.INVALID_VERIFICATION_CODE));

        // 동일한지 확인
        if (!verification.getCode().equals(code)) {
            throw new ErrorDomain(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        // 만료 시간 확인
        if (verification.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new ErrorDomain(ErrorCode.INVALID_VERIFICATION_CODE);
        }
    }

}
