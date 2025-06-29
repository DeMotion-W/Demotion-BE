// DemoController.java
package com.example.Demotion.Domain.Demo.Controller;

import com.example.Demotion.Common.ErrorCode;
import com.example.Demotion.Common.ErrorDomain;
import com.example.Demotion.Domain.Auth.Entity.User;
import com.example.Demotion.Domain.Demo.Dto.*;
import com.example.Demotion.Domain.Demo.Entity.Demo;
import com.example.Demotion.Domain.Demo.Repository.DemoRepository;
import com.example.Demotion.Domain.Demo.Service.DemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demos")
@RequiredArgsConstructor
public class DemoController {

    private final DemoService demoService;
    private final DemoRepository demoRepository;

    // 데모 생성
    @PostMapping
    public ResponseEntity<CreateDemoResponseDto> createDemo(
            @AuthenticationPrincipal User user,
            @RequestBody CreateDemoRequestDto request
    ) {
        Long id = demoService.createDemo(request, user.getId());
        return ResponseEntity.ok(new CreateDemoResponseDto(id));
    }

    // 데모 수정
    @PutMapping("/{demoId}")
    public ResponseEntity<CommonResponse> updateDemo(
            @PathVariable Long demoId,
            @AuthenticationPrincipal User user,
            @RequestBody UpdateDemoRequestDto request
    ) {
        demoService.updateDemo(demoId, user.getId(), request);
        return ResponseEntity.ok(new CommonResponse(true));
    }

    // 특정 데모 조회
    @GetMapping("/{demoId}")
    public ResponseEntity<DemoDetailResponseDto> getDemoDetail(
            @PathVariable Long demoId,
            @AuthenticationPrincipal User user
    ) {
        DemoDetailResponseDto dto = demoService.getDemoDetail(demoId, user.getId());
        return ResponseEntity.ok(dto);
    }

    // 데모 리스트 조회
    @GetMapping
    public ResponseEntity<List<DemoSummaryDto>> getDemoList(
            @AuthenticationPrincipal User user
    ) {
        List<DemoSummaryDto> demos = demoService.getDemoList(user.getId());
        return ResponseEntity.ok(demos);
    }

    // 데모 삭제
    @DeleteMapping("/{demoId}")
    public ResponseEntity<CommonResponse> deleteDemo(
            @PathVariable Long demoId,
            @AuthenticationPrincipal User user
    ) {
        demoService.deleteDemo(demoId, user.getId());
        return ResponseEntity.ok(new CommonResponse(true));
    }
}
