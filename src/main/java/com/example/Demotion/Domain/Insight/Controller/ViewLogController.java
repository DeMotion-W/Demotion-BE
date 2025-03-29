package com.example.Demotion.Domain.Insight.Controller;

import com.example.Demotion.Domain.Insight.Entity.ViewLog;
import com.example.Demotion.Domain.Insight.Service.ViewLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/view-log")
public class ViewLogController {

    private final ViewLogService viewLogService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody ViewLog log) {
        viewLogService.save(log);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/finish")
    public ResponseEntity<Void> finish(@PathVariable Long id) {
        viewLogService.markAsFinished(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats/{demoId}")
    public ResponseEntity<Map<String, Object>> stats(@PathVariable Long demoId) {
        return ResponseEntity.ok(viewLogService.getStats(demoId));
    }
}
