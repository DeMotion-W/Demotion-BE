package com.example.Demotion.Domain.Insight.Service;

import com.example.Demotion.Common.ErrorCode;
import com.example.Demotion.Common.ErrorDomain;
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

@Service
@RequiredArgsConstructor
public class InsightService {

    private final DemoRepository demoRepository;
    private final ViewerSessionRepository sessionRepository;
    private final ViewerEventRepository eventRepository;

    /**
     * 데모에 대한 시청 통계를 계산해 반환
     * - 총 조회수: 이메일 입력 후 데모를 시작한 세션 수
     * - 완주율: 마지막 스크린샷까지 본 세션 비율
     * - 각 스크린샷에 대한 조회수 및 평균 체류 시간
     */
    public InsightStatResponseDto.Response getStat(Long demoId, Long userId) {
        Demo demo = demoRepository.findById(demoId)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.DEMO_NOT_FOUND));

        if (!demo.getUser().getId().equals(userId)) {
            throw new ErrorDomain(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        List<ViewerSession> sessions = sessionRepository.findAllByDemoId(demoId);
        List<ViewerEvent> events = eventRepository.findAllBySession_DemoId(demoId);

        int viewCount = sessions.size();
        int completedCount = 0;
        Map<Long, List<Long>> durationMap = new HashMap<>();
        Map<Long, Integer> viewCountMap = new HashMap<>();

        List<Long> allScreenshotIds = demo.getScreenshots().stream()
                .map(s -> s.getId())
                .toList();

        Long lastScreenshotId = demo.getScreenshots().get(demo.getScreenshots().size() - 1).getId();

        for (ViewerSession session : sessions) {
            List<ViewerEvent> evts = events.stream()
                    .filter(e -> e.getSession().getId().equals(session.getId()))
                    .sorted(Comparator.comparingLong(ViewerEvent::getTimestampMillis))
                    .toList();

            for (ViewerEvent e : evts) {
                Long sid = e.getScreenshotId();
                viewCountMap.put(sid, viewCountMap.getOrDefault(sid, 0) + 1);
            }

            for (int i = 1; i < evts.size(); i++) {
                Long prevId = evts.get(i - 1).getScreenshotId();
                Long currId = evts.get(i).getScreenshotId();
                if (Objects.equals(prevId, currId)) continue;

                long duration = evts.get(i).getTimestampMillis() - evts.get(i - 1).getTimestampMillis();
                durationMap.computeIfAbsent(currId, k -> new ArrayList<>()).add(duration);
            }

            if (!evts.isEmpty() && Objects.equals(evts.get(evts.size() - 1).getScreenshotId(), lastScreenshotId)) {
                completedCount++;
            }
        }

        double completionRate = viewCount == 0 ? 0 : ((double) completedCount * 100) / viewCount;

        List<InsightStatResponseDto.Response.ScreenshotStat> stats = new ArrayList<>();
        for (Long sid : allScreenshotIds) {
            int count = viewCountMap.getOrDefault(sid, 0);
            List<Long> durations = durationMap.getOrDefault(sid, null);
            Long avgDuration = (durations == null || durations.isEmpty())
                    ? null
                    : Math.round(durations.stream().mapToLong(l -> l).average().orElse(0));
            stats.add(new InsightStatResponseDto.Response.ScreenshotStat(sid, count, avgDuration));
        }

        return new InsightStatResponseDto.Response(viewCount, completionRate, stats);
    }

    /**
     * 해당 데모에 입력된 리드(이메일) 목록을 반환합니다.
     * 각 리드가 CTA(도입 문의) 버튼을 클릭했는지도 함께 반환합니다.
     */
    public InsightLeadResponseDto.Response getLeads(Long demoId, Long userId) {
        Demo demo = demoRepository.findById(demoId)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.DEMO_NOT_FOUND));

        if (!demo.getUser().getId().equals(userId)) {
            throw new ErrorDomain(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        List<ViewerSession> sessions = sessionRepository.findAllByDemoId(demoId);
        List<InsightLeadResponseDto.Response.Lead> leads = sessions.stream()
                .map(s -> new InsightLeadResponseDto.Response.Lead(s.getEmail(), s.isContactClicked()))
                .toList();

        return new InsightLeadResponseDto.Response(leads);
    }

    /**
     * 데모 존재 여부 및 로그인 사용자의 접근 권한 검증
     * - demoId에 해당하는 Demo가 존재하는지
     * - 요청 유저가 해당 Demo의 소유자인지
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