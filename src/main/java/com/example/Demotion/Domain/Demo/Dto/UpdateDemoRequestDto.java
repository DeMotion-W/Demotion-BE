package com.example.Demotion.Domain.Demo.Dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateDemoRequestDto {

    private String title;
    private String subtitle;
    private String buttonBgColor;
    private String buttonTextColor;
    private List<ScreenshotUpdateRequest> screenshots;

    @Data
    public static class ScreenshotUpdateRequest {
        private Long screenshotId;
        private String buttonText;
        private String buttonBgColor;
        private String buttonTextColor;
        private String buttonStyle;
    }
}
