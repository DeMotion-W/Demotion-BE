package com.example.Demotion.Domain.Insight.Controller;

import com.example.Demotion.Common.ErrorCode;
import com.example.Demotion.Common.ErrorDomain;
import com.example.Demotion.Domain.Insight.Dto.InsightLeadResponseDto;
import com.example.Demotion.Domain.Insight.Dto.InsightStatResponseDto;
import com.example.Demotion.Domain.Insight.Dto.StayTimeDto;
import com.example.Demotion.Domain.Insight.Service.ViewerQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.Demotion.Domain.Auth.Entity.User;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/demos")
public class InsightController {

    private final ViewerQueryService insightService;

    // 데모 인사이트 요약 통계
    @GetMapping("/{demoId}/insight/stat")
    public ResponseEntity<InsightStatResponseDto.Response> getStat(@PathVariable Long demoId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(insightService.getStat(demoId, user.getId()));
    }

    // 데모 리드 (이메일 & CTA 클릭 여부) 조회
    @GetMapping("/{demoId}/insight/leads")
    public ResponseEntity<InsightLeadResponseDto.Response> getLeads(
            @PathVariable Long demoId,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new ErrorDomain(ErrorCode.MISSING_AUTHORIZATION_HEADER);
        }

        return ResponseEntity.ok(insightService.getLeads(demoId, user.getId()));
    }

    // 세션별 체류시간 반환
    @GetMapping("/sessions/{sessionId}/stay-times")
    public ResponseEntity<List<StayTimeDto>> getStayTimes(@PathVariable Long sessionId) {
        return ResponseEntity.ok(insightService.getStayTimesForSession(sessionId));
    }
}
