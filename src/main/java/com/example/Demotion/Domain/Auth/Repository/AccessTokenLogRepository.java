package com.example.Demotion.Domain.Auth.Repository;

import com.example.Demotion.Domain.Auth.Entity.AccessTokenLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessTokenLogRepository extends JpaRepository<AccessTokenLog, Long> {
}
