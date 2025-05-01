package com.example.Demotion.Domain.ImgFile.Controller;

import com.example.Demotion.Domain.ImgFile.Dto.PreSignedUrlRequestDto;
import com.example.Demotion.Domain.ImgFile.Dto.PreSignedUrlResponseDto;
import com.example.Demotion.Domain.ImgFile.Service.S3UploaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class ImgFileController {

    private final S3UploaderService s3UploaderService;

    @PostMapping("/pre-signed-urls")
    public ResponseEntity<PreSignedUrlResponseDto> getPreSignedUrls(@RequestBody PreSignedUrlRequestDto request) {
        List<String> urls = request.fileNames().stream()
                .map(fileName -> s3UploaderService.generatePreSignedUrl("screenshots/" + fileName).toString())
                .toList();

        return ResponseEntity.ok(new PreSignedUrlResponseDto(urls));
    }
}
