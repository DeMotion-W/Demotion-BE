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
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@RequiredArgsConstructor
public class ViewerCommandService {

    private final DemoRepository demoRepository;
    private final ViewerSessionRepository sessionRepository;
    private final ViewerEventRepository eventRepository;
    private final ScreenshotRepository screenshotRepository;

    private final BlockingQueue<Runnable> commandQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // 세션 생성
    @PostConstruct
    public void initQueueProcessor() {
        executor.submit(() -> {
            while (true) {
                try {
                    Runnable task = commandQueue.take();
                    task.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    public void startSessionAsync(Long demoId, String email) {
        commandQueue.offer(() -> startSessionSync(demoId, email));
    }

    private void startSessionSync(Long demoId, String email) {
        Demo demo = demoRepository.findById(demoId)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.DEMO_NOT_FOUND));
        ViewerSession session = new ViewerSession();
        session.setDemo(demo);
        session.setEmail(email);
        sessionRepository.save(session);
    }

    public void recordStepAsync(Long sessionId, Long demoId, Long screenshotId, Long timestampMillis) {
        commandQueue.offer(() -> recordStepSync(sessionId, demoId, screenshotId, timestampMillis));
    }

    private void recordStepSync(Long sessionId, Long demoId, Long screenshotId, Long timestampMillis) {
        // 기존 recordStep 로직 그대로
        ViewerSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.DEMO_NOT_FOUND));

        if (!session.getDemo().getId().equals(demoId)) {
            throw new ErrorDomain(ErrorCode.DEMO_NOT_FOUND);
        }

        // 이전 이벤트가 있으면 stayTimeMillis 계산
        eventRepository.findTopBySessionIdOrderByTimestampMillisDesc(sessionId).ifPresent(previous -> {
            long stayTime = timestampMillis - previous.getTimestampMillis();
            previous.setStayTimeMillis(stayTime);
            eventRepository.save(previous);
        });

        // 새 이벤트 생성
        ViewerEvent event = new ViewerEvent();
        event.setSession(session);
        event.setTimestampMillis(timestampMillis);
        event.setStayTimeMillis(null); // 아직 체류 시간 없음

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

    public void recordContactClickAsync(Long sessionId, Long demoId) {
        commandQueue.offer(() -> recordContactClickSync(sessionId, demoId));
    }

    private void recordContactClickSync(Long sessionId, Long demoId) {
        // 기존 recordContactClick 로직 그대로
        ViewerSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.USER_NOT_FOUND));

        Demo demo = demoRepository.findById(demoId)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.DEMO_NOT_FOUND));

        if (!session.getDemo().getId().equals(demoId)) {
            throw new ErrorDomain(ErrorCode.UNAUTHORIZED_ACCESS);  // 세션이 해당 데모에 속하지 않음
        }

        try {
            session.setContactClicked(true); // "도입문의 클릭 여부" 필드가 있다고 가정
            sessionRepository.save(session);
        } catch (Exception e) {
            throw new ErrorDomain(ErrorCode.DEMO_DB_SAVE_FAILED); // DB 저장 실패
        }
    }
}
