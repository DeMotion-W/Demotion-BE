package com.example.Demotion.Domain.Demo.Dto;

import java.util.List;

public record PreSignedUrlResponseDto(List<FileInfo> files) {
    public record FileInfo(
            String originalFileName,
            String uploadUrl,
            String fileUrl
    ) {}
}
