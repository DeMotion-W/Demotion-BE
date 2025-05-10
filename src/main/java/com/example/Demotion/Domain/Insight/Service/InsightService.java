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
        Demo demo = validateDemoAccess(demoId, userId);

        List<ViewerSession> sessions = sessionRepository.findAllByDemoId(demoId);
        List<ViewerEvent> events = eventRepository.findAllBySession_DemoId(demoId);

        int viewCount = sessions.size(); // [전체 조회수] 중복 포함? -> 확인 필요

        int completedCount = 0; // [완주율] 세션 중복 포함 -> 확인 필요
        Map<Long, List<Long>> durationMap = new HashMap<>(); // screenshotId -> [durations]
        Map<Long, Integer> viewCountMap = new HashMap<>();   // screenshotId -> view count

        // 모든 스크린샷 ID를 미리 확보 (0부터 N까지 포함)
        List<Long> allScreenshotIds = demo.getScreenshots().stream()
                .map(s -> s.getId())
                .toList();

        Long lastScreenshotId = demo.getScreenshots().get(demo.getScreenshots().size() - 1).getId(); // 데모의 마지막 스크린샷 ID

        // 데모를 조회한 각 세션별로 차례로
        for (ViewerSession session : sessions) {
            List<ViewerEvent> evts = events.stream()
                    .filter(e -> e.getSession().getId().equals(session.getId()))
                    .sorted(Comparator.comparingLong(ViewerEvent::getTimestampMillis)) // 시간순 이벤트 정렬
                    .toList();

            // 스크린샷 조회수 집계
            for (ViewerEvent e : evts) {
                Long sid = e.getScreenshotId(); // 작은 수의 id부터
                viewCountMap.put(sid, viewCountMap.getOrDefault(sid, 0) + 1); // 해당 스크린샷 조회수 올리기
            }

            // 스크린샷 체류시간 집계
            for (int i = 1; i < evts.size(); i++) {
                Long prevId = evts.get(i - 1).getScreenshotId();
                Long currId = evts.get(i).getScreenshotId();
                if (Objects.equals(prevId, currId)) continue;

                long duration = evts.get(i).getTimestampMillis() - evts.get(i - 1).getTimestampMillis();
                durationMap.computeIfAbsent(currId, k -> new ArrayList<>()).add(duration);
            }

            // 마지막 스크린샷까지 본 경우 완주로 간주
            if (!evts.isEmpty() && Objects.equals(evts.get(evts.size() - 1).getScreenshotId(), lastScreenshotId)) {
                completedCount++;
            }
        }

        // 완주율 계산
        double completionRate = viewCount == 0 ? 0 : ((double) completedCount * 100) / viewCount;

        // 모든 스크린샷에 대해 평균 체류시간 및 조회수 계산 (screenshotId 0 포함)
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
        Demo demo = validateDemoAccess(demoId, userId);

        // 해당 데모에 연결된 모든 세션에서 이메일 + CTA 클릭 여부 추출
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