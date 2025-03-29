package com.example.Demotion.Domain.Insight.Service;

import com.example.Demotion.Domain.Insight.Entity.ViewLog;
import com.example.Demotion.Domain.Insight.Repository.ViewLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ViewLogService {

    private final ViewLogRepository viewLogRepository;

    public void save(ViewLog log) {
        log.setCreatedAt(LocalDateTime.now());
        log.setFinished(false);
        viewLogRepository.save(log);
    }

    public void markAsFinished(Long id) {
        ViewLog log = viewLogRepository.findById(id).orElseThrow();
        log.setFinished(true);
        viewLogRepository.save(log);
    }

    public Map<String, Object> getStats(Long demoId) {
        long total = viewLogRepository.countByDemoId(demoId);
        long unique = viewLogRepository.countDistinctByDemoId(demoId);
        long finished = viewLogRepository.countByDemoIdAndFinishedTrue(demoId);
        double completionRate = total == 0 ? 0 : (finished * 100.0) / total;

        return Map.of(
                "viewCount", total,
                "uniqueViewCount", unique,
                "finishedCount", finished,
                "completionRate", completionRate
        );
    }
}
