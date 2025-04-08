package com.example.Demotion.Domain.Insight.Controller;

import com.example.Demotion.Domain.Insight.Entity.StepViewLog;
import com.example.Demotion.Domain.Insight.Service.StepViewLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/step-view-log")
public class StepViewLogController {

    private final StepViewLogService stepViewLogService;

    // 시청 기록 저장
    @PostMapping
    public ResponseEntity<Void> save(@RequestBody StepViewLog log) {
        stepViewLogService.save(log);
        return ResponseEntity.ok().build();
    }

    // 스텝별 조회수 통계
    @GetMapping("/count")
    public ResponseEntity<Map<Long, Long>> count(@RequestParam List<Long> stepIds) {
        return ResponseEntity.ok(stepViewLogService.getStepViewCounts(stepIds));
    }

    // 스텝별 평균 체류시간 통계
    @GetMapping("/duration")
    public ResponseEntity<Map<Long, Double>> duration(@RequestParam List<Long> stepIds) {
        return ResponseEntity.ok(stepViewLogService.getStepAverageDurations(stepIds));
    }

    // 데모 기준 전체 스텝 통계 조회
    @GetMapping("/stats/{demoId}")
    public ResponseEntity<List<Map<String, Object>>> getStats(@PathVariable Long demoId) {
        return ResponseEntity.ok(stepViewLogService.getStatsByDemoId(demoId));
    }
}