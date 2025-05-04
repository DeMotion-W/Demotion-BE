package com.example.Demotion.Domain.Demo.Dto;


import lombok.Data;

import java.util.List;

@Data
public class CreateDemoRequestDto {

    private String title;
    private String subtitle;
    private List<ScreenshotRequest> screenshots;

    @Data
    public static class ScreenshotRequest {
        private String fileUrl;
        private String buttonText;
        private String buttonColor;
        private String buttonStyle; // nullable
        private int positionX;
        private int positionY;
    }
}
