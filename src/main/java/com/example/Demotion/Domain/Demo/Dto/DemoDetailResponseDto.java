package com.example.Demotion.Domain.Demo.Dto;

import com.example.Demotion.Domain.Demo.Entity.Demo;
import com.example.Demotion.Domain.Demo.Entity.Screenshot;
import lombok.Data;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class DemoDetailResponseDto {

    private Long demoId;
    private String title;
    private String subTitle;
    private String buttonBgColor;
    private String buttonTextColor;
    private List<ScreenshotDto> screenshots;

    public static DemoDetailResponseDto fromEntity(Demo demo) {
        DemoDetailResponseDto dto = new DemoDetailResponseDto();
        dto.setDemoId(demo.getId());
        dto.setTitle(demo.getTitle());
        dto.setSubTitle(demo.getSubtitle());
        dto.setButtonBgColor(demo.getButtonBgColor());
        dto.setButtonTextColor(demo.getButtonTextColor());

        List<ScreenshotDto> screenshotDtos = demo.getScreenshots().stream()
                .map(ScreenshotDto::fromEntity)// 정렬된 screenshot 객체 하나씩 받아서 dto로 변환
                .collect(Collectors.toList());// stream으로 흘려보낸 결과물 다시 list로 수집

        dto.setScreenshots(screenshotDtos);
        return dto;
    }

    @Data
    public static class ScreenshotDto {
        private Long screenshotId;
        private String fileUrl;
        private String buttonText;
        private String buttonBgColor;
        private String buttonTextColor;
        private String buttonStyle;
        private float positionX;
        private float positionY;

        public static ScreenshotDto fromEntity(Screenshot screenshot) {
            ScreenshotDto dto = new ScreenshotDto();
            dto.setScreenshotId(screenshot.getId());
            dto.setFileUrl(screenshot.getFileUrl());
            dto.setButtonText(screenshot.getButtonText());
            dto.setButtonBgColor(screenshot.getButtonBgColor());
            dto.setButtonTextColor(screenshot.getButtonTextColor());
            dto.setButtonStyle(screenshot.getButtonStyle());
            dto.setPositionX(screenshot.getPositionX());
            dto.setPositionY(screenshot.getPositionY());
            return dto;
        }
    }
}
