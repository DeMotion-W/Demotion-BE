package com.example.Demotion.Domain.Auth.Repository;

import com.example.Demotion.Domain.Auth.Entity.EmailVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, Long> {
    Optional<EmailVerificationCode> findTopByEmailOrderByCreatedAtDesc(String email);
    Optional<EmailVerificationCode> deleteAllByEmail(String email);
}
