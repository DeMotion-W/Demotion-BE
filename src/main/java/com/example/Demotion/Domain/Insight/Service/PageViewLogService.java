package com.example.Demotion.Domain.Insight.Service;

import com.example.Demotion.Domain.Insight.Entity.PageViewLog;
import com.example.Demotion.Domain.Insight.Entity.ViewLog;
import com.example.Demotion.Domain.Insight.Repository.PageViewLogRepository;
import com.example.Demotion.Domain.Insight.Repository.ViewLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class PageViewLogService {

    private final PageViewLogRepository pageViewLogRepository;

    public void save(PageViewLog log) {
        log.setViewedAt(LocalDateTime.now());
        pageViewLogRepository.save(log);
    }

    public Map<Integer, Long> getStats(Long demoId) {
        List<Object[]> results = pageViewLogRepository.countPageViewsByDemoId(demoId);

        Map<Integer, Long> stats = new HashMap<>();
        for (Object[] row : results) {
            stats.put((Integer) row[0], (Long) row[1]);
        }
        return stats;
    }
}

