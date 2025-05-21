package com.example.Demotion.Domain.Demo.Service;

import com.example.Demotion.Common.ErrorCode;
import com.example.Demotion.Common.ErrorDomain;
import com.example.Demotion.Domain.Auth.Entity.User;
import com.example.Demotion.Domain.Auth.Repository.UserRepository;
import com.example.Demotion.Domain.Demo.Dto.CreateDemoRequestDto;
import com.example.Demotion.Domain.Demo.Dto.DemoDetailResponseDto;
import com.example.Demotion.Domain.Demo.Dto.DemoSummaryDto;
import com.example.Demotion.Domain.Demo.Dto.UpdateDemoRequestDto;
import com.example.Demotion.Domain.Demo.Entity.Demo;
import com.example.Demotion.Domain.Demo.Entity.Screenshot;
import com.example.Demotion.Domain.Demo.Repository.DemoRepository;
import com.example.Demotion.Domain.Demo.Repository.ScreenshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DemoService {

    private final DemoRepository demoRepository;
    private final ScreenshotRepository screenshotRepository;
    private final UserRepository userRepository;

    public Long createDemo(CreateDemoRequestDto request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.USER_NOT_FOUND));

        if (request.getTitle() == null || request.getSubtitle() == null || request.getScreenshots() == null || request.getScreenshots().isEmpty()) {
            throw new ErrorDomain(ErrorCode.MISSING_REQUIRED_FIELDS);
        }

        try {
            Demo demo = new Demo();
            demo.setTitle(request.getTitle());
            demo.setSubtitle(request.getSubtitle());
            demo.setButtonBgColor(request.getButtonBgColor());
            demo.setButtonTextColor(request.getButtonTextColor());
            demo.setUser(user);

            var screenshots = request.getScreenshots().stream().map(s -> {
                Screenshot ss = new Screenshot();
                ss.setFileUrl(s.getFileUrl());
                ss.setButtonText(s.getButtonText());
                ss.setButtonBgColor(s.getButtonBgColor());
                ss.setButtonTextColor(s.getButtonTextColor());
                ss.setButtonStyle(s.getButtonStyle());
                ss.setPositionX(s.getPositionX());
                ss.setPositionY(s.getPositionY());
                ss.setDemo(demo);
                return ss;
            }).collect(Collectors.toList());

            demo.setScreenshots(screenshots);
            return demoRepository.save(demo).getId();

        } catch (Exception e) {
            throw new ErrorDomain(ErrorCode.DEMO_DB_SAVE_FAILED);
        }
    }

    @Transactional
    public void updateDemo(Long demoId, Long userId, UpdateDemoRequestDto request) {
        Demo demo = demoRepository.findById(demoId)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.DEMO_NOT_FOUND));

        if (!demo.getUser().getId().equals(userId)) {
            throw new ErrorDomain(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        demo.setTitle(request.getTitle());
        demo.setSubtitle(request.getSubtitle());
        demo.setButtonBgColor(request.getButtonBgColor());
        demo.setButtonTextColor(request.getButtonTextColor());

        for (UpdateDemoRequestDto.ScreenshotUpdateRequest ssReq : request.getScreenshots()) {
            Screenshot ss = screenshotRepository.findByIdAndDemoId(ssReq.getScreenshotId(), demoId)
                    .orElseThrow(() -> new ErrorDomain(ErrorCode.SCREENSHOT_NOT_FOUND));
            ss.setButtonText(ssReq.getButtonText());
            ss.setButtonBgColor(ssReq.getButtonBgColor());
            ss.setButtonTextColor(ssReq.getButtonTextColor());
            ss.setButtonStyle(ssReq.getButtonStyle());
        }
    }

    @Transactional
    public DemoDetailResponseDto getDemoDetail(Long demoId, Long userId) {
        Demo demo = demoRepository.findById(demoId)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.DEMO_NOT_FOUND));

        if (!demo.getUser().getId().equals(userId)) {
            throw new ErrorDomain(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        DemoDetailResponseDto dto = new DemoDetailResponseDto();
        dto.setDemoId(demo.getId());
        dto.setTitle(demo.getTitle());
        dto.setSubTitle(demo.getSubtitle());
        dto.setButtonBgColor(demo.getButtonBgColor());
        dto.setButtonTextColor(demo.getButtonTextColor());

        List<DemoDetailResponseDto.ScreenshotDto> screenshots = demo.getScreenshots().stream()
                .map(s -> {
                    DemoDetailResponseDto.ScreenshotDto ssDto = new DemoDetailResponseDto.ScreenshotDto();
                    ssDto.setScreenshotId(s.getId());
                    ssDto.setFileUrl(s.getFileUrl());
                    ssDto.setButtonText(s.getButtonText());
                    ssDto.setButtonBgColor(s.getButtonBgColor());
                    ssDto.setButtonTextColor(s.getButtonTextColor());
                    ssDto.setButtonStyle(s.getButtonStyle());
                    ssDto.setPositionX(s.getPositionX());
                    ssDto.setPositionY(s.getPositionY());
                    return ssDto;
                })
                .collect(Collectors.toList());

        dto.setScreenshots(screenshots);
        return dto;
    }

    public List<DemoSummaryDto> getDemoList(Long userId) {
        try {
            return demoRepository.findAllByUserId(userId).stream()
                    .map(demo -> {
                        String firstImageUrl = demo.getScreenshots().stream()
                                .findFirst()
                                .map(Screenshot::getFileUrl)
                                .orElse(null);

                        return new DemoSummaryDto(
                                demo.getId(),
                                demo.getTitle(),
                                firstImageUrl,
                                demo.getCreatedAt()
                        );
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ErrorDomain(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteDemo(Long demoId, Long userId) {
        Demo demo = demoRepository.findById(demoId)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.DEMO_NOT_FOUND));

        if (!demo.getUser().getId().equals(userId)) {
            throw new ErrorDomain(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        try {
            demoRepository.delete(demo);
        } catch (Exception e) {
            throw new ErrorDomain(ErrorCode.DEMO_DELETE_FAILED);
        }
    }
}


