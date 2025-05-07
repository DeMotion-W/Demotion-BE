package com.example.Demotion.Domain.Insight.Dto;

import java.util.List;

public class InsightStatResponseDto{
    public record Response(
            int viewCount,
            double completionRate,
            List<ScreenshotStat> screenshotStats
    ) {
        public record ScreenshotStat(Long screenshotId, int viewCount, long avgDurationMillis) {}
    }
}
