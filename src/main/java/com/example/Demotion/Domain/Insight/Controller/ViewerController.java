package com.example.Demotion.Domain.Insight.Controller;

import com.example.Demotion.Common.ErrorCode;
import com.example.Demotion.Common.ErrorDomain;
import com.example.Demotion.Common.Slack.Service.SlackService;
import com.example.Demotion.Domain.Auth.Entity.User;
import com.example.Demotion.Domain.Demo.Entity.Demo;
import com.example.Demotion.Domain.Demo.Repository.DemoRepository;
import com.example.Demotion.Domain.Insight.Dto.StayTimeDto;
import com.example.Demotion.Domain.Insight.Service.ViewerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/embed")
public class ViewerController {

    private final ViewerService viewerService;
    private final SlackService slackService;
    private final DemoRepository demoRepository;

    // 데모 조회 시작 (이메일 입력 시)
    @PostMapping("/{demoId}/start")
    public ResponseEntity<Map<String, Long>> startDemo(
            @PathVariable Long demoId,
            @RequestBody Map<String, String> request
    ) {
        String email = request.get("email");

        if (email == null || email.isBlank()) {
            throw new ErrorDomain(ErrorCode.MISSING_REQUIRED_FIELDS);
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ErrorDomain(ErrorCode.INVALID_EMAIL);
        }

        Demo demo = demoRepository.findById(demoId)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.DEMO_NOT_FOUND));
        User user = demo.getUser();

        Long sessionId = viewerService.startSession(demoId, email);
        slackService.sendMessage(user.getId(), "✅ 누군가 데모를 완료했습니다!");
        return ResponseEntity.ok(Map.of("sessionId", sessionId));
    }

    // 스크린샷 진입 (버튼 클릭 순간)
    @PostMapping("/{demoId}/step")
    public ResponseEntity<Map<String, String>> recordStep(
            @PathVariable Long demoId,
            @RequestBody Map<String, Object> request
    ) {
        if (!request.containsKey("sessionId") || !request.containsKey("screenshotId") || !request.containsKey("timestampMillis")) {
            throw new ErrorDomain(ErrorCode.MISSING_REQUIRED_FIELDS);
        }

        try {
            Long sessionId = Long.valueOf(request.get("sessionId").toString());
            Long screenshotId = Long.valueOf(request.get("screenshotId").toString());
            Long timestampMillis = Long.valueOf(request.get("timestampMillis").toString());

            viewerService.recordStep(sessionId, demoId, screenshotId, timestampMillis);
            return ResponseEntity.ok(Map.of("message", "success"));
        } catch (NumberFormatException e) {
            throw new ErrorDomain(ErrorCode.MISSING_REQUIRED_FIELDS);
        }
    }

    // 도입 문의 버튼 클릭
    @PostMapping("/{demoId}/contact")
    public ResponseEntity<Void> recordContactClick(
            @PathVariable Long demoId,
            @RequestBody Map<String, Object> request
    ) {
        Long sessionId = Long.valueOf(request.get("sessionId").toString());
        viewerService.recordContactClick(sessionId, demoId);
        return ResponseEntity.ok().build();
    }

    // 세션별 체류시간 반환
    @GetMapping("/sessions/{sessionId}/stay-times")
    public ResponseEntity<List<StayTimeDto>> getStayTimes(@PathVariable Long sessionId) {
        return ResponseEntity.ok(viewerService.getStayTimesForSession(sessionId));
    }

}
