package com.example.Demotion.Domain.Demo.Service;

import com.example.Demotion.Common.ErrorCode;
import com.example.Demotion.Common.ErrorDomain;
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

    @Value("${cloud.aws.region.static}")
    private String region;

    // preSigned Url 생성
    public URL generatePreSignedUrl(String key) {
        validateFileExtension(key);

        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .putObjectRequest(objectRequest)
                    .signatureDuration(Duration.ofMinutes(10))
                    .build();

            return s3Presigner.presignPutObject(presignRequest).url();
        } catch (Exception e) {
            throw new ErrorDomain(ErrorCode.PRESIGNED_URL_GENERATION_FAILED);
        }
    }

    // 객체 url 조회
    public String getObjectUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);
    }

    // 확장자 확인
    private void validateFileExtension(String key) {
        if (!(key.endsWith(".png") || key.endsWith(".jpg") || key.endsWith(".jpeg"))) {
            throw new ErrorDomain(ErrorCode.INVALID_FILE_FORMAT);
        }
    }
}

