package com.Foodie.restaurant_service.service.S3;

import com.Foodie.restaurant_service.utils.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(
            MultipartFile file,
            String folder,
            String filename
    ){
        try {
            String key = folder + "/" + filename;

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

            return  String.format("http://localhost:9000/%s/%s", bucketName, key);
        }
        catch (IOException e){
            throw new RuntimeException(ErrorMessage.FILED_TO_UPLOAD_FILE.getMessage(e.getMessage()));
        }
    }

    public void deleteFile(
            String fileUrl
    ){
        String key = extractKeyFromUrl(fileUrl);
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build()
        );
    }

    private String extractKeyFromUrl(
            String url
    ){
        return url.substring(url.indexOf(bucketName) + bucketName.length() + 1);
    }
}
