package com.example.Demotion.Domain.Insight.Service;

import com.example.Demotion.Domain.Demo.Entity.Demo;
import com.example.Demotion.Domain.Demo.Repository.DemoRepository;
import com.example.Demotion.Domain.Insight.Dto.InsightLeadResponseDto;
import com.example.Demotion.Domain.Insight.Dto.InsightStatResponseDto;
import com.example.Demotion.Domain.Insight.Entity.ViewerEvent;
import com.example.Demotion.Domain.Insight.Entity.ViewerSession;
import com.example.Demotion.Domain.Insight.Repository.ViewerEventRepository;
import com.example.Demotion.Domain.Insight.Repository.ViewerSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

// === 리팩토링된 InsightService ===
@Service
@RequiredArgsConstructor
public class InsightService {

    private final DemoRepository demoRepository;
    private final ViewerSessionRepository sessionRepository;
    private final ViewerEventRepository eventRepository;

    /**
     * 특정 데모의 통계를 조회합니다.
     * 조회수, 완주율, 스크린샷별 조회수 및 평균 체류시간 반환
     */
    public InsightStatResponseDto.Response getStat(Long demoId, Long userId) {
        Demo demo = validateDemoAccess(demoId, userId);

        List<ViewerSession> sessions = sessionRepository.findAllByDemoId(demoId);
        List<ViewerEvent> events = eventRepository.findAllBySession_DemoId(demoId);

        int viewCount = sessions.size();
        Set<Long> completedSessionIds = new HashSet<>();
        Map<Long, List<Long>> durations = new HashMap<>();

        for (ViewerSession session : sessions) {
            List<ViewerEvent> evts = events.stream()
                    .filter(e -> e.getSession().getId().equals(session.getId()))
                    .sorted(Comparator.comparingLong(ViewerEvent::getTimestampMillis))
                    .toList();

            for (int i = 1; i < evts.size(); i++) {
                Long sid = evts.get(i - 1).getScreenshotId();
                long d = evts.get(i).getTimestampMillis() - evts.get(i - 1).getTimestampMillis();
                durations.computeIfAbsent(sid, k -> new ArrayList<>()).add(d);
            }

            // 마지막 스크린샷까지 본 경우 완주 처리
            if (!evts.isEmpty()) {
                Long lastScreenshotId = demo.getScreenshots().get(demo.getScreenshots().size() - 1).getId();
                Long lastSeenId = evts.get(evts.size() - 1).getScreenshotId();
                if (Objects.equals(lastScreenshotId, lastSeenId)) {
                    completedSessionIds.add(session.getId());
                }
            }
        }

        double completionRate = viewCount == 0 ? 0 : ((double) completedSessionIds.size() * 100) / viewCount;

        List<InsightStatResponseDto.Response.ScreenshotStat> stats = durations.entrySet().stream()
                .map(e -> {
                    long avg = (long) e.getValue().stream().mapToLong(l -> l).average().orElse(0);
                    return new InsightStatResponseDto.Response.ScreenshotStat(e.getKey(), e.getValue().size(), avg);
                }).toList();

        return new InsightStatResponseDto.Response(viewCount, completionRate, stats);
    }

    /**
     * 특정 데모에 등록된 리드 목록을 반환합니다.
     */
    public InsightLeadResponseDto.Response getLeads(Long demoId, Long userId) {
        Demo demo = validateDemoAccess(demoId, userId);

        List<ViewerSession> sessions = sessionRepository.findAllByDemoId(demoId);
        List<InsightLeadResponseDto.Response.Lead> leads = sessions.stream()
                .map(s -> new InsightLeadResponseDto.Response.Lead(s.getEmail(), s.isContactClicked()))
                .toList();

        return new InsightLeadResponseDto.Response(leads);
    }

    /**
     * 데모 존재 여부와 접근 권한을 검증합니다.
     */
    private Demo validateDemoAccess(Long demoId, Long userId) {
        Demo demo = demoRepository.findById(demoId)
                .orElseThrow(() -> new RuntimeException("Demo not found"));

        if (!demo.getUser().getId().equals(userId)) {
            throw new RuntimeException("No access to this demo");
        }
        return demo;
    }
}