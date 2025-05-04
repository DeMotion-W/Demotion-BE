package com.example.Demotion.Domain.Demo.Controller;

import com.example.Demotion.Domain.Auth.Entity.User;
import com.example.Demotion.Domain.Demo.Dto.*;
import com.example.Demotion.Domain.Demo.Service.DemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demos")
@RequiredArgsConstructor
public class DemoController {

    private final DemoService demoService;

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
            @AuthenticationPrincipal User userDetails,
            @RequestBody UpdateDemoRequestDto request
    ) {
        Long userId = ((User) userDetails).getId();
        demoService.updateDemo(demoId, userId, request);
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

    // 데모 조회
    @GetMapping
    public ResponseEntity<List<DemoDetailResponseDto>> getDemoList(
            @AuthenticationPrincipal User user
    ) {
        List<DemoDetailResponseDto> demos = demoService.getDemoList(user.getId());
        return ResponseEntity.ok(demos);
    }

    // 데모 삭제
    @DeleteMapping("/{demoId}")
    public ResponseEntity<CommonResponse> deleteDemo(
        @PathVariable Long demoId,
        @AuthenticationPrincipal User user
    ){
        demoService.deleteDemo(demoId, user.getId());
        return ResponseEntity.ok(new CommonResponse(true));
    }
}
