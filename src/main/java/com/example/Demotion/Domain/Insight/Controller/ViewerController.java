package com.example.Demotion.Domain.Insight.Controller;

import com.example.Demotion.Common.ErrorCode;
import com.example.Demotion.Common.ErrorDomain;
import com.example.Demotion.Common.Slack.Service.SlackService;
import com.example.Demotion.Domain.Auth.Entity.User;
import com.example.Demotion.Domain.Demo.Entity.Demo;
import com.example.Demotion.Domain.Demo.Repository.DemoRepository;
import com.example.Demotion.Domain.Insight.Service.ViewerCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/embed")
public class ViewerController {

    private final ViewerCommandService viewerCommandService;

    // 데모 조회 시작 (이메일 입력 시)
    @PostMapping("/{demoId}/start")
    public ResponseEntity<Map<String, String>> startDemo(
            @PathVariable Long demoId,
            @RequestBody Map<String, String> request) {
        String email = request.get("email");
        // 유효성 검사 생략
        viewerCommandService.startSessionAsync(demoId, email);
        return ResponseEntity.accepted().body(Map.of("message", "세션 생성 요청 처리 중"));
    }

    // 스크린샷 진입 (버튼 클릭 순간)
    @PostMapping("/{demoId}/step")
    public ResponseEntity<Map<String, String>> recordStep(
            @PathVariable Long demoId,
            @RequestBody Map<String, Object> request) {
        // 유효성 검사 생략
        Long sessionId = Long.valueOf(request.get("sessionId").toString());
        Long screenshotId = Long.valueOf(request.get("screenshotId").toString());
        Long timestampMillis = Long.valueOf(request.get("timestampMillis").toString());
        viewerCommandService.recordStepAsync(sessionId, demoId, screenshotId, timestampMillis);
        return ResponseEntity.accepted().body(Map.of("message", "step 기록 요청 처리 중"));
    }

    // 도입 문의 버튼 클릭
    @PostMapping("/{demoId}/contact")
    public ResponseEntity<Map<String, String>> recordContactClick(
            @PathVariable Long demoId,
            @RequestBody Map<String, Object> request) {
        Long sessionId = Long.valueOf(request.get("sessionId").toString());
        viewerCommandService.recordContactClickAsync(sessionId, demoId);
        return ResponseEntity.accepted().body(Map.of("message", "Contact 클릭 요청 처리 중"));
    }
}
