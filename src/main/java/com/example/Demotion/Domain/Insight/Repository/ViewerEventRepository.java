package com.example.Demotion.Domain.Insight.Repository;

import com.example.Demotion.Domain.Insight.Entity.ViewerEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ViewerEventRepository extends JpaRepository<ViewerEvent, Long> {
    List<ViewerEvent> findBySessionId(Long sessionId);
    List<ViewerEvent> findAllBySession_DemoId(Long demoId);
    List<ViewerEvent> findBySessionIdOrderByTimestampMillisAsc(Long sessionId);
    Optional<ViewerEvent> findTopBySessionIdOrderByTimestampMillisDesc(Long sessionId);
}
