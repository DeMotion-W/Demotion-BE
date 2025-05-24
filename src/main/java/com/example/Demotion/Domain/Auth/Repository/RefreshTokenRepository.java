package com.example.Demotion.Domain.Auth.Repository;

import com.example.Demotion.Domain.Auth.Entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByEmail(String email);

    @Transactional
    void deleteByEmail(String email);

    boolean existsByEmail(String email);
}
