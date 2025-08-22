package com.dobongsoon.BuyDobong.common.s3;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service{
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region}")
    private String region;

    @Value("${file.upload.store-dir:stores}")
    private String storeDir;

    @Value("${file.upload.allowed-content-types:image/jpeg,image/png,image/webp}")
    private String allowedContentTypes;

    private Set<String> allowed() {
        return Set.of(allowedContentTypes.split(","));
    }

    @Override
    public String uploadStoreImage(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        String contentType = file.getContentType();
        if (contentType == null || !allowed().contains(contentType)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        String ext = switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png"  -> "png";
            case "image/webp" -> "webp";
            default -> "bin";
        };

        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String key = "%s/%s/u-%d/%s.%s".formatted(storeDir, datePath, userId, UUID.randomUUID(), ext);

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        try {
            s3Client.putObject(req, RequestBody.fromBytes(file.getBytes()));
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return publicUrl(key);
    }

    @Override
    public void delete(String key) {
        if (key == null || key.isBlank()) return;
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
        } catch (S3Exception ignored) { }
    }

    private String publicUrl(String key) {
        String encKey = URLEncoder.encode(key, StandardCharsets.UTF_8).replace("+", "%20");
        return "https://%s.s3.%s.amazonaws.com/%s".formatted(bucket, region, encKey);
    }
}
