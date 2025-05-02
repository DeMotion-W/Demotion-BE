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
//DB와 통신해서 조회기록을 저장하거나 조회 수를 계산하주는 리포지토리 객체
    private final PageViewLogRepository pageViewLogRepository;

    public void save(PageViewLog log) {
        log.setViewedAt(LocalDateTime.now()); //현재시각을 VIEWAT필들에 기록
        pageViewLogRepository.save(log); //엔티티를 DB에 저장
    }

    public Map<Integer, Long> getStats(Long demoId) { // 레퍼지토리에서 JPQL 퀄리를 실행해서 가져온다.
        List<Object[]> results = pageViewLogRepository.countPageViewsByDemoId(demoId);

        Map<Integer, Long> stats = new HashMap<>();//쿼리를 반복하면서 MAP에 넣기
        for (Object[] row : results) {
            stats.put((Integer) row[0], (Long) row[1]);
        }
        return stats;
    }
}

