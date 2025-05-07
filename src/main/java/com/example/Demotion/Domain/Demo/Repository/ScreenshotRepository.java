package com.example.Demotion.Domain.Demo.Repository;

import com.example.Demotion.Domain.Demo.Entity.Screenshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreenshotRepository extends JpaRepository<Screenshot, Long> {
}
