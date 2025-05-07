package com.example.Demotion.Domain.Demo.Controller;

import com.example.Demotion.Domain.Demo.Dto.DemoDetailResponseDto;
import com.example.Demotion.Domain.Demo.Service.EmbedDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/embed")
@RequiredArgsConstructor
public class EmbedDemoController {

    private final EmbedDemoService embedDemoService;

    // 데모 조회 (익명의 user용)
    @GetMapping("/embed/{publicId}")
    public ResponseEntity<DemoDetailResponseDto> getPublicDemo(@PathVariable String publicId) {
        DemoDetailResponseDto dto = embedDemoService.getPublicDemoDetail(publicId);
        return ResponseEntity.ok(dto);
    }
}
