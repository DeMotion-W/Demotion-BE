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

@RestController
@RequestMapping("/api/demos")
@RequiredArgsConstructor
public class DemoController {

    private final DemoService demoService;

    @PostMapping
    public ResponseEntity<CreateDemoResponseDto> createDemo(
            @AuthenticationPrincipal User user,
            @RequestBody CreateDemoRequestDto request
    ) {
        Long id = demoService.createDemo(request, user.getId());
        return ResponseEntity.ok(new CreateDemoResponseDto(id));
    }

    @PutMapping("/{demoId}")
    public ResponseEntity<CommonResponse> updateDemo(
            @PathVariable Long demoId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateDemoRequestDto request
    ) {
        Long userId = ((User) userDetails).getId();  // 명시적으로 캐스팅
        demoService.updateDemo(demoId, userId, request);
        return ResponseEntity.ok(new CommonResponse(true));
    }


    @GetMapping("/{demoId}")
    public ResponseEntity<DemoDetailResponseDto> getDemoDetail(
            @PathVariable Long demoId,
            @AuthenticationPrincipal User user
    ) {
        DemoDetailResponseDto dto = demoService.getDemoDetail(demoId, user.getId());
        return ResponseEntity.ok(dto);
    }

}
