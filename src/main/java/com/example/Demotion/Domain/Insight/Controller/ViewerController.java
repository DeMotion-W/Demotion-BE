package com.example.Demotion.Domain.Insight.Controller;

import com.example.Demotion.Domain.Insight.Service.ViewerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/embed")
public class ViewerController {

    private final ViewerService viewerService;

    // 데모 조회 시작 (이메일 입력 시)
    @PostMapping("/{publicId}/start")
    public ResponseEntity<Map<String, Long>> startDemo(
            @PathVariable String publicId,
            @RequestBody Map<String, String> request
    ) {
        String email = request.get("email");
        Long sessionId = viewerService.startSession(publicId, email);
        return ResponseEntity.ok(Map.of("sessionId", sessionId));
    }

    // 스크린샷 진입 (버튼 클릭 순간)
    @PostMapping("/{publicId}/step")
    public ResponseEntity<Void> recordStep(
            @PathVariable String publicId,
            @RequestBody Map<String, Object> request
    ) {
        Long sessionId = Long.valueOf(request.get("sessionId").toString());
        Long screenshotId = Long.valueOf(request.get("screenshotId").toString());
        Long timestampMillis = Long.valueOf(request.get("timestampMillis").toString());

        viewerService.recordStep(sessionId, publicId, screenshotId, timestampMillis);
        return ResponseEntity.ok().build();
    }

    // 도입 문의 버튼 클릭
    @PostMapping("/{publicId}/contact")
    public ResponseEntity<Void> recordContactClick(
            @PathVariable String publicId,
            @RequestBody Map<String, Object> request
    ) {
        Long sessionId = Long.valueOf(request.get("sessionId").toString());
        viewerService.recordContactClick(sessionId, publicId);
        return ResponseEntity.ok().build();
    }
}
