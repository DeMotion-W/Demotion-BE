package com.example.Demotion.Domain.Demo.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;

@RequiredArgsConstructor
@Service
public class S3UploaderService {

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public URL generatePreSignedUrl(String key) {
        //String contentType = determineContentType(key); // 확장자에 따라 MIME 타입 결정

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(objectRequest)
                .signatureDuration(Duration.ofMinutes(10))
                .build();

        return s3Presigner.presignPutObject(presignRequest).url();
    }
//
//    private String determineContentType(String key) {
//        if (key.endsWith(".png")) return "image/png";
//        if (key.endsWith(".jpg") || key.endsWith(".jpeg")) return "image/jpeg";
//        return "application/octet-stream"; // 기본값
//    }
}

