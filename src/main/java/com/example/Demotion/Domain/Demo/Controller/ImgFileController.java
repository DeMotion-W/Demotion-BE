package com.example.Demotion.Domain.Demo.Controller;

import com.example.Demotion.Domain.Demo.Dto.PreSignedUrlRequestDto;
import com.example.Demotion.Domain.Demo.Dto.PreSignedUrlResponseDto;
import com.example.Demotion.Domain.Demo.Service.S3UploaderService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.FileInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ImgFileController {

    private final S3UploaderService s3UploaderService;

    @PostMapping("/presigned-urls")
    public ResponseEntity<PreSignedUrlResponseDto> getPreSignedUrls(@RequestBody PreSignedUrlRequestDto request) {
        List<PreSignedUrlResponseDto.FileInfo> files = request.fileNames().stream()
                .map(fileName -> {
                    String key = "screenshots/" + fileName;
                    String uploadUrl = s3UploaderService.generatePreSignedUrl(key).toString();
                    String fileUrl = s3UploaderService.getObjectUrl(key);
                    return new PreSignedUrlResponseDto.FileInfo(fileName, uploadUrl, fileUrl);
                })
                .toList();

        return ResponseEntity.ok(new PreSignedUrlResponseDto(files));
    }
}
