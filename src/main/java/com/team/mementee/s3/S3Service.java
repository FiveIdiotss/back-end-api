package com.team.mementee.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.team.mementee.api.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final AmazonS3 amazonS3;
    private final FileService fileService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String saveMemberProFile(MultipartFile multipartFile) {
        return saveToS3ForMember(multipartFile, bucket);
    }

    private String saveToS3ForMember(MultipartFile file, String bucketName) {
        try {
            return saveToS3(file.getInputStream(), file.getSize(), file.getName(), bucketName, fileService.getExtension(file));
        } catch (IOException e) {
            log.error("Error uploading to S3: {}", e.getMessage());
            return "Error uploading file: " + e.getMessage();
        }
    }

    public String saveFile(MultipartFile multipartFile) {
        return saveToS3(multipartFile, bucket);
    }

    public String getImageUrl(String imageName) {
        try {
            S3Object s3Object = amazonS3.getObject(bucket, imageName);
            return (s3Object != null) ? amazonS3.getUrl(bucket, imageName).toString() : null;
        } catch (Exception e) {
            log.error("Error fetching image URL: {}", e.getMessage(), e);
            return null;
        }
    }

    public String save(MultipartFile file) {
        String bucketName = "fiveidiots-" + fileService.extractFileType(file).toString().toLowerCase();
        return saveToS3(file, bucketName);
    }

    private String saveToS3(MultipartFile file, String bucketName) {
        try {
            return saveToS3(file.getInputStream(), file.getSize(), file.getContentType(), bucketName, fileService.getExtension(file));
        } catch (IOException e) {
            log.error("Error uploading to S3: {}", e.getMessage());
            return "Error uploading file: " + e.getMessage();
        }
    }

    private String saveToS3(InputStream inputStream, Long contentLength, String contentType, String bucketName, String fileName) {
        ObjectMetadata metadata = createObjectMetaData(contentLength, contentType);
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    public String saveMultipartFile(MultipartFile file) {
        return save(file);
    }

    private ObjectMetadata createObjectMetaData(Long contentLength, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        metadata.setContentType(contentType);
        return metadata;
    }
}