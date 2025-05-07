package com.example.Demotion.Domain.Demo.Service;

import com.example.Demotion.Domain.Demo.Dto.DemoDetailResponseDto;
import com.example.Demotion.Domain.Demo.Entity.Demo;
import com.example.Demotion.Domain.Demo.Repository.DemoRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmbedDemoService {

    private final DemoRepository demoRepository;

    public DemoDetailResponseDto getPublicDemoDetail(String publicId) {
        Demo demo = demoRepository.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("데모가 존재하지 않습니다."));

        DemoDetailResponseDto dto = new DemoDetailResponseDto();
        dto.setDemoId(demo.getId());
        dto.setTitle(demo.getTitle());
        dto.setSubTitle(demo.getSubtitle());

        List<DemoDetailResponseDto.ScreenshotDto> screenshots = demo.getScreenshots().stream()
                .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                .map(s -> {
                    DemoDetailResponseDto.ScreenshotDto ssDto = new DemoDetailResponseDto.ScreenshotDto();
                    ssDto.setScreenshotId(s.getId());
                    ssDto.setFileUrl(s.getFileUrl());
                    ssDto.setOrder(s.getOrder());
                    ssDto.setButtonText(s.getButtonText());
                    ssDto.setButtonColor(s.getButtonColor());
                    ssDto.setButtonStyle(s.getButtonStyle());
                    ssDto.setPositionX(s.getPositionX());
                    ssDto.setPositionY(s.getPositionY());
                    return ssDto;
                })
                .collect(Collectors.toList());

        dto.setScreenshots(screenshots);
        return dto;
    }
}
