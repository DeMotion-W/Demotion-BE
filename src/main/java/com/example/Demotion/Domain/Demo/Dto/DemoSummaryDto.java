package com.example.Demotion.Domain.Demo.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
public class DemoSummaryDto {
    private Long demoId;
    private String title;
    private String firstScreenshotUrl;
    private LocalDateTime createdAt;
}
