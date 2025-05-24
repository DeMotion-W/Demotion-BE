package com.example.Demotion.Common;

public record ErrorResponse(
        int code,
        String message,
        Object data
) {}
