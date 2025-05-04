package com.example.Demotion.Domain.Demo.Dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateDemoRequestDto {

    private String title;
    private String subtitle;
    private List<ScreenshotUpdateRequest> screenshots;

    @Data
    public static class ScreenshotUpdateRequest {
        private Long screenshotId;
        private String buttonText;
        private String buttonColor;
        private String buttonStyle;
    }
}
