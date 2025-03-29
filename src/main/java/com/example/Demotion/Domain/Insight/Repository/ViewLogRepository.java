package com.example.Demotion.Domain.Insight.Repository;

import com.example.Demotion.Domain.Insight.Entity.ViewLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewLogRepository extends JpaRepository<ViewLog, Long> {
    long countByDemoId(Long demoId);
    long countDistinctByDemoId(Long demoId);
    long countByDemoIdAndFinishedTrue(Long demoId);
}
