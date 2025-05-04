package com.example.Demotion.Domain.Demo.Dto;

import lombok.Data;

import java.util.List;

@Data
public class DemoDetailResponseDto {

    private Long demoId;
    private String title;
    private String subTitle;
    private List<ScreenshotDto> screenshots;

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
    }
}
