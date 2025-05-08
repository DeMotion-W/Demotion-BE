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
    private String publicId;
    private String title;
    private String subTitle;
    private List<ScreenshotDto> screenshots;

    public static DemoDetailResponseDto fromEntity(Demo demo) {
        DemoDetailResponseDto dto = new DemoDetailResponseDto();
        dto.setDemoId(demo.getId());
        dto.setPublicId(demo.getPublicId());
        dto.setTitle(demo.getTitle());
        dto.setSubTitle(demo.getSubtitle());

        List<ScreenshotDto> screenshotDtos = demo.getScreenshots().stream()
                .sorted(Comparator.comparingInt(Screenshot::getOrder)) // order 기준으로 오름차순 정렬
                .map(ScreenshotDto::fromEntity)// 정렬된 screenshot 객체 하나씩 받아서 dto로 변환
                .collect(Collectors.toList());// stream으로 흘려보낸 결과물 다시 list로 수집

        dto.setScreenshots(screenshotDtos);
        return dto;
    }

    @Data
    public static class ScreenshotDto {
        private Long screenshotId;
        private String fileUrl;
        private int order;
        private String buttonText;
        private String buttonColor;
        private String buttonStyle;
        private float positionX;
        private float positionY;

        public static ScreenshotDto fromEntity(Screenshot screenshot) {
            ScreenshotDto dto = new ScreenshotDto();
            dto.setScreenshotId(screenshot.getId());
            dto.setFileUrl(screenshot.getFileUrl());
            dto.setOrder(screenshot.getOrder());
            dto.setButtonText(screenshot.getButtonText());
            dto.setButtonColor(screenshot.getButtonColor());
            dto.setButtonStyle(screenshot.getButtonStyle());
            dto.setPositionX(screenshot.getPositionX());
            dto.setPositionY(screenshot.getPositionY());
            return dto;
        }
    }
}
