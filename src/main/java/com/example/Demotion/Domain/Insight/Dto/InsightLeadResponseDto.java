package com.example.Demotion.Domain.Insight.Dto;

import java.util.List;

public class InsightLeadResponseDto{
    public record Response(
            List<Lead> leads
    ) {
        public record Lead(String email, boolean contactClicked) {}
    }}
