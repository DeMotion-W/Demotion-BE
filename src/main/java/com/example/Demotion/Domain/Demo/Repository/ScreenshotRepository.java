package com.example.Demotion.Domain.Demo.Repository;

import com.example.Demotion.Domain.Demo.Entity.Screenshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScreenshotRepository extends JpaRepository<Screenshot, Long> {
    Optional<Screenshot> findByIdAndDemoId(Long screenshotId, Long demoId);
    Optional<Screenshot> findByIdAndDemo_PublicId(Long screenshotId, String publicId);
}
