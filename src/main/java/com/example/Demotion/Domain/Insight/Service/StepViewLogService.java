package com.example.Demotion.Domain.Insight.Service;

import com.example.Demotion.Domain.Insight.Entity.StepViewLog;
import com.example.Demotion.Domain.Insight.Repository.StepViewLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StepViewLogService {

    private final StepViewLogRepository stepViewLogRepository;

    public void save(StepViewLog log) {
        log.setViewedAt(LocalDateTime.now());
        stepViewLogRepository.save(log);
    }

    public Map<Long, Long> getStepViewCounts(List<Long> stepIds) {
        List<Object[]> result = stepViewLogRepository.countByStepIds(stepIds);
        Map<Long, Long> map = new HashMap<>();
        for (Object[] row : result) {
            map.put((Long) row[0], (Long) row[1]);
        }
        return map;
    }

    public Map<Long, Double> getStepAverageDurations(List<Long> stepIds) {
        List<Object[]> result = stepViewLogRepository.averageDurationByStepIds(stepIds);
        Map<Long, Double> map = new HashMap<>();
        for (Object[] row : result) {
            map.put((Long) row[0], (Double) row[1]);
        }
        return map;
    }

    // ✅ demoId 기준으로 모든 스텝별 통계 반환 (viewCount + avgDuration)
    public List<Map<String, Object>> getStatsByDemoId(Long demoId) {
        List<Object[]> rows = stepViewLogRepository.findStatsByDemoId(demoId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("stepId", row[0]);
            entry.put("viewCount", row[1]);
            entry.put("avgDuration", row[2]);
            result.add(entry);
        }
        return result;
    }
}