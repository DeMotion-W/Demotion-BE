package com.example.Demotion.Domain.Insight.Service;

import com.example.Demotion.Common.ErrorCode;
import com.example.Demotion.Common.ErrorDomain;
import com.example.Demotion.Domain.Demo.Entity.Demo;
import com.example.Demotion.Domain.Demo.Entity.Screenshot;
import com.example.Demotion.Domain.Demo.Repository.DemoRepository;
import com.example.Demotion.Domain.Demo.Repository.ScreenshotRepository;
import com.example.Demotion.Domain.Insight.Entity.ViewerEvent;
import com.example.Demotion.Domain.Insight.Entity.ViewerSession;
import com.example.Demotion.Domain.Insight.Repository.ViewerEventRepository;
import com.example.Demotion.Domain.Insight.Repository.ViewerSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ViewerService {

    private final DemoRepository demoRepository;
    private final ViewerSessionRepository sessionRepository;
    private final ViewerEventRepository eventRepository;
    private final ScreenshotRepository screenshotRepository;

    // 세션 생성
    public Long startSession(Long demoId, String email) {
        Demo demo = demoRepository.findById(demoId)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.DEMO_NOT_FOUND));

        ViewerSession session = new ViewerSession();
        session.setDemo(demo);
        session.setEmail(email);

        return sessionRepository.save(session).getId();
    }

    // 세션별 이벤트 생성
    public void recordStep(Long sessionId, Long demoId, Long screenshotId, Long timestampMillis) {

        ViewerSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.DEMO_NOT_FOUND));

        if (!session.getDemo().getId().equals(demoId)) {
            throw new ErrorDomain(ErrorCode.DEMO_NOT_FOUND);
        }

        ViewerEvent event = new ViewerEvent();
        event.setSession(session);
        event.setTimestampMillis(timestampMillis);

        if (screenshotId == 0L) {
            event.setScreenshotId(0L);
        } else {
            Screenshot screenshot = screenshotRepository.findById(screenshotId)
                    .orElseThrow(() -> new ErrorDomain(ErrorCode.DEMO_NOT_FOUND));

            if (!screenshot.getDemo().getId().equals(demoId)) {
                throw new ErrorDomain(ErrorCode.DEMO_NOT_FOUND);
            }

            event.setScreenshotId(screenshot.getId());
        }

        eventRepository.save(event);
    }

    // 세션 도입 버튼 클릭 여부 설정
    @Transactional
    public void recordContactClick(Long sessionId, Long demoId) {
        ViewerSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getDemo().getId().equals(demoId)) {
            throw new RuntimeException("Session does not belong to this demo");
        }

        session.setContactClicked(true);
        sessionRepository.save(session);
    }
}
