package com.dobongsoon.BuyDobong.common.s3;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String uploadStoreImage(Long userId, MultipartFile file);
    void delete(String key);
}
