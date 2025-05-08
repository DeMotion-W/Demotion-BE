package com.example.Demotion.Domain.Demo.Service;

import com.example.Demotion.Domain.Auth.Entity.User;
import com.example.Demotion.Domain.Auth.Repository.UserRepository;
import com.example.Demotion.Domain.Demo.Dto.CreateDemoRequestDto;
import com.example.Demotion.Domain.Demo.Dto.DemoDetailResponseDto;
import com.example.Demotion.Domain.Demo.Dto.UpdateDemoRequestDto;
import com.example.Demotion.Domain.Demo.Entity.Demo;
import com.example.Demotion.Domain.Demo.Entity.Screenshot;
import com.example.Demotion.Domain.Demo.Repository.DemoRepository;
import com.example.Demotion.Domain.Demo.Repository.ScreenshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DemoService {

    private final DemoRepository demoRepository;
    private final ScreenshotRepository screenshotRepository;
    private final UserRepository userRepository;

    // 데모 생성
    public Long createDemo(CreateDemoRequestDto request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Demo demo = new Demo();
        demo.setTitle(request.getTitle());
        demo.setSubtitle(request.getSubtitle());
        demo.setUser(user); // 🔥 사용자 연관관계 설정

        var screenshots = request.getScreenshots().stream().map(s -> {
            Screenshot ss = new Screenshot();
            ss.setFileUrl(s.getFileUrl());
            ss.setButtonText(s.getButtonText());
            ss.setButtonColor(s.getButtonColor());
            ss.setButtonStyle(s.getButtonStyle());
            ss.setPositionX(s.getPositionX());
            ss.setPositionY(s.getPositionY());
            ss.setDemo(demo); // 연관관계 설정
            return ss;
        }).collect(Collectors.toList());

        demo.setScreenshots(screenshots);

        return demoRepository.save(demo).getId();
    }

    // 데모 수정
    @Transactional
    public void updateDemo(Long demoId, Long userId, UpdateDemoRequestDto request) {
        Demo demo = demoRepository.findById(demoId)
                .orElseThrow(() -> new RuntimeException("Demo not found"));

        if (!demo.getUser().getId().equals(userId)) {
            throw new RuntimeException("No permission to edit this demo");
        }

        demo.setTitle(request.getTitle());
        demo.setSubtitle(request.getSubtitle());

        for (UpdateDemoRequestDto.ScreenshotUpdateRequest ssReq : request.getScreenshots()) {
            Screenshot ss = screenshotRepository.findById(ssReq.getScreenshotId())
                    .orElseThrow(() -> new RuntimeException("Screenshot not found"));
            ss.setButtonText(ssReq.getButtonText());
            ss.setButtonColor(ssReq.getButtonColor());
            ss.setButtonStyle(ssReq.getButtonStyle());
        }
    }

    // 특정 데모 조회
    @Transactional
    public DemoDetailResponseDto getDemoDetail(Long demoId, Long userId) {
        Demo demo = demoRepository.findById(demoId)
                .orElseThrow(() -> new RuntimeException("데모가 존재하지 않습니다."));

        if (!demo.getUser().getId().equals(userId)) {
            throw new RuntimeException("해당 데모에 접근할 수 없습니다.");
        }

        DemoDetailResponseDto dto = new DemoDetailResponseDto();
        dto.setDemoId(demo.getId());
        dto.setPublicId(demo.getPublicId());
        dto.setTitle(demo.getTitle());
        dto.setSubTitle(demo.getSubtitle());

        List<DemoDetailResponseDto.ScreenshotDto> screenshots = demo.getScreenshots().stream()
                .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                .map(s -> {
                    DemoDetailResponseDto.ScreenshotDto ssDto = new DemoDetailResponseDto.ScreenshotDto();
                    ssDto.setScreenshotId(s.getId());
                    ssDto.setFileUrl(s.getFileUrl());
                    ssDto.setOrder(0); // order 필드 있다면 적용
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

    // 데모 조회
    public List<DemoDetailResponseDto> getDemoList(Long userId) {
        List<Demo> demoList = demoRepository.findAllByUserId(userId);

        return demoList.stream()
                .map(DemoDetailResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 특정 데모 삭제
    public void deleteDemo(Long demoId, Long userId) {
        Demo demo = demoRepository.findById(demoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 데모가 존재하지 않습니다."));

        if (!demo.getUser().getId().equals(userId)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        demoRepository.delete(demo);
    }
}

