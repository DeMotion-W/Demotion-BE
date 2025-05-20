package com.example.Demotion.Common.Slack.Repository;

import com.example.Demotion.Common.Slack.Entity.SlackIntegration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SlackIntegrationRepository extends JpaRepository<SlackIntegration, Long> {
    Optional<SlackIntegration> findByUserId(Long userId);
}
