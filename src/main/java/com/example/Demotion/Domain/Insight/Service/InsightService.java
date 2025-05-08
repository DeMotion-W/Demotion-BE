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

        List<ViewerSession> sessions = sessionRepository.findAllByDemoId(demoId); // 해당 데모 조회한 모든 세션들
        List<ViewerEvent> events = eventRepository.findAllBySession_DemoId(demoId); // 특정 데모 ID와 연결된 모든 세션에 속한 모든 이벤트 세션들

        int viewCount = sessions.size(); // [조회수] 총 시청 세션 수

        Set<Long> completedSessionIds = new HashSet<>(); // 완주한 세션 ID 저장용
        Map<Long, List<Long>> durations = new HashMap<>(); // 각 스크린샷별 체류시간 목록

        // 각 세션마다 이벤트를 시간순으로 정렬해 분석
        for (ViewerSession session : sessions) {
            List<ViewerEvent> evts = events.stream()
                    .filter(e -> e.getSession().getId().equals(session.getId())) // 해당 세션에 속한 이벤트만 필터링
                    .sorted(Comparator.comparingLong(ViewerEvent::getTimestampMillis)) // 시간순 정렬
                    .toList();

            // 현재 스크린샷 ID 기준으로 체류시간 측정 (현재 - 이전 클릭 시각)
            for (int i = 1; i < evts.size(); i++) {
                Long currentScreenshotId = evts.get(i).getScreenshotId();
                long duration = evts.get(i).getTimestampMillis() - evts.get(i - 1).getTimestampMillis();
                durations.computeIfAbsent(currentScreenshotId, k -> new ArrayList<>()).add(duration); // 리스트에 체류시간 추가
            }

            // 세션이 마지막 스크린샷까지 도달했는지 확인하여 완주 처리
            if (!evts.isEmpty()) {
                Long lastScreenshotId = demo.getScreenshots().get(demo.getScreenshots().size() - 1).getId();
                Long lastSeenId = evts.get(evts.size() - 1).getScreenshotId();
                if (Objects.equals(lastScreenshotId, lastSeenId)) {
                    completedSessionIds.add(session.getId()); // 완주했으면 리스트에 추가
                }
            }
        }

        // [완주율] 계산: (완주한 세션 수 / 전체 세션 수) * 100
        double completionRate = viewCount == 0 ? 0 : ((double) completedSessionIds.size() * 100) / viewCount;

        // [각 스크린샷별 평균 체류 시간 및 조회수] 계산
        List<InsightStatResponseDto.Response.ScreenshotStat> stats = durations.entrySet().stream()
                .map(entry -> {
                    long avgDuration = (long) entry.getValue().stream().mapToLong(l -> l).average().orElse(0);
                    return new InsightStatResponseDto.Response.ScreenshotStat(
                            entry.getKey(), entry.getValue().size(), avgDuration); // screenshotId, 조회수, 평균 체류시간
                }).toList();

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