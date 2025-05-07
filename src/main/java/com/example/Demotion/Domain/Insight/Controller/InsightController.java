package com.example.Demotion.Domain.Insight.Controller;

import com.example.Demotion.Domain.Insight.Dto.InsightLeadResponseDto;
import com.example.Demotion.Domain.Insight.Dto.InsightStatResponseDto;
import com.example.Demotion.Domain.Insight.Service.InsightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.Demotion.Domain.Auth.Entity.User;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/demos/{demoId}/insight")
public class InsightController {

    private final InsightService insightService;

    // 데모 인사이트 요약 통계
    @GetMapping("/stat")
    public ResponseEntity<InsightStatResponseDto.Response> getStat(@PathVariable Long demoId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(insightService.getStat(demoId, user.getId()));
    }

    // 데모 리드 (이메일 & CTA 클릭 여부) 조회
    @GetMapping("/leads")
    public ResponseEntity<InsightLeadResponseDto.Response> getLeads(@PathVariable Long demoId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(insightService.getLeads(demoId, user.getId()));
    }
}
