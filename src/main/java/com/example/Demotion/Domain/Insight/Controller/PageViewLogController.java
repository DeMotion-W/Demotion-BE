package com.example.Demotion.Domain.Insight.Controller;

import com.example.Demotion.Domain.Insight.Entity.PageViewLog;
import com.example.Demotion.Domain.Insight.Entity.ViewLog;
import com.example.Demotion.Domain.Insight.Service.PageViewLogService;
import com.example.Demotion.Domain.Insight.Service.ViewLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/page-view-log")
public class PageViewLogController {

    private final PageViewLogService pageViewLogService;

    // [1] 페이지 조회 기록 저장
    @PostMapping
    public ResponseEntity<Void> save(@RequestBody PageViewLog log) {
        pageViewLogService.save(log);
        return ResponseEntity.ok().build();
    }

    // [2] 데모별 페이지별 조회수 통계 조회
    @GetMapping("/stats/{demoId}")
    public ResponseEntity<Map<Integer, Long>> stats(@PathVariable Long demoId) {
        return ResponseEntity.ok(pageViewLogService.getStats(demoId));
    }
}

