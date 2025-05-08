package com.example.Demotion.Domain.Insight.Service;

import com.example.Demotion.Domain.Demo.Entity.Demo;
import com.example.Demotion.Domain.Demo.Repository.DemoRepository;
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

    // 세션 생성
    public Long startSession(String publicId, String email) {
        Demo demo = demoRepository.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("Demo not found"));

        ViewerSession session = new ViewerSession();
        session.setDemo(demo);
        session.setEmail(email);

        return sessionRepository.save(session).getId();
    }

    // 세션별 이벤트 생성
    public void recordStep(Long sessionId, String publicId, Long screenshotId, Long timestampMillis) {
        ViewerSession session = sessionRepository.findByIdAndDemo_PublicId(sessionId, publicId)
                .orElseThrow(() -> new RuntimeException("Session not found or does not match publicId"));

        ViewerEvent event = new ViewerEvent();
        event.setSession(session);
        event.setScreenshotId(screenshotId);
        event.setTimestampMillis(timestampMillis);

        eventRepository.save(event);
    }

    // 세션 도입 버튼 클릭 여부 설정
    @Transactional
    public void recordContactClick(Long sessionId, String publicId) {
        ViewerSession session = sessionRepository.findByIdAndDemo_PublicId(sessionId, publicId)
                .orElseThrow(() -> new RuntimeException("Session not found or does not match publicId"));

        session.setContactClicked(true);
        sessionRepository.save(session);
    }
}
