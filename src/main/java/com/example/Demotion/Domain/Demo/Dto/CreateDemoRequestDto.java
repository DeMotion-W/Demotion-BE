package com.example.Demotion.Domain.Demo.Dto;


import lombok.Data;

import java.util.List;

@Data
public class CreateDemoRequestDto {

    private String title;
    private String subtitle;
    private String buttonBgColor;
    private String buttonTextColor;
    private List<ScreenshotRequest> screenshots;

    @Data
    public static class ScreenshotRequest {
        private String fileUrl;
        private String buttonText;
        private String buttonBgColor;
        private String buttonTextColor;
        private String buttonStyle; // nullable
        private double positionX;
        private double positionY;
    }
}
