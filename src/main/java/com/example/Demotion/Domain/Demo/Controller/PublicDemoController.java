package com.example.Demotion.Domain.Demo.Controller;

import com.example.Demotion.Common.ErrorCode;
import com.example.Demotion.Common.ErrorDomain;
import com.example.Demotion.Domain.Demo.Dto.DemoDetailResponseDto;
import com.example.Demotion.Domain.Demo.Entity.Demo;
import com.example.Demotion.Domain.Demo.Repository.DemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/demos")
@RequiredArgsConstructor
public class PublicDemoController {

    private final DemoRepository demoRepository;

    @GetMapping("/{publicId}")
    public ResponseEntity<DemoDetailResponseDto> getPublicDemo(@PathVariable String publicId) {
        Demo demo = demoRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.DEMO_NOT_FOUND));

        return ResponseEntity.ok(DemoDetailResponseDto.fromEntity(demo));
    }
}
