package com.example.Demotion.Domain.Insight.Repository;

import com.example.Demotion.Domain.Insight.Entity.ViewerSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ViewerSessionRepository extends JpaRepository<ViewerSession, Long> {
    Optional<ViewerSession> findByIdAndDemo_PublicId(Long id, String publicId);
    List<ViewerSession> findAllByDemoId(Long demoId);
}
