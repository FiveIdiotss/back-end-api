package com.team.mementee.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.team.mementee.api.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import static com.team.mementee.api.service.FileService.createObjectMetaData;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final AmazonS3 amazonS3;
    private final FileService fileService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.s3.bucket_image}")
    private String bucket_image;
    @Value("${cloud.aws.s3.bucket_video}")
    private String bucket_video;
    @Value("${cloud.aws.s3.bucket_pdf}")
    private String bucket_pdf;

    @Value("${cloud.aws.s3.bucket_temp}")
    private String tempBucket;


    public String saveFile(MultipartFile multipartFile) {
        return saveToS3(multipartFile, bucket);
    }

    //originalFilename 로 조회
    public String getImageUrl(String imageName) {
        try {
            S3Object s3Object = amazonS3.getObject(bucket, imageName);

            if (s3Object != null) return amazonS3.getUrl(bucket, imageName).toString();
            else return null;

        } catch (Exception e) {
            return null;
        }
    }

    public String save(MultipartFile file) {
        String bucketName = "fiveidiots-" + fileService.getFileType(file.getContentType()).toString().toLowerCase();
        System.out.println(bucketName);
        return saveToS3(file, bucketName);
    }

    private String saveToS3(MultipartFile file, String bucketName) {
        String extension = fileService.getExtension(file.getOriginalFilename());
        String fileName = fileService.generateFileName(extension);
        try {
            return saveToS3(file.getInputStream(), file.getSize(), file.getContentType(), bucketName, fileName);
        } catch (IOException e) {
            log.error("Error uploading to S3: {}", e.getMessage(), e);
            return "Error uploading file: " + e.getMessage();
        }
    }

    private String saveToS3(InputStream inputStream, Long contentLength, String contentType, String bucketName, String fileName) {
        ObjectMetadata metadata = createObjectMetaData(contentLength, contentType);
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));

        return amazonS3.getUrl(bucketName, fileName).toString();
    }


    //임시 버킷
    public String saveFileToTemp(MultipartFile multipartFile) {
        System.out.println();
        return saveToS3(multipartFile, tempBucket);
    }

    //영구 버킷으로 이동 후 임시 버킷 옮긴 자료는 삭제
    public String moveFileToPermanent(String tempFileUrl) {
        try {
            String tempFileName = fileService.extractFileNameFromUrl(tempFileUrl); // URL에서 파일 이름 추출

            String permanentFileName = fileService.generateFileName(fileService.getExtension(tempFileName));
            CopyObjectRequest copyObjRequest = new CopyObjectRequest(tempBucket, tempFileName, bucket, permanentFileName);
            amazonS3.copyObject(copyObjRequest);
            amazonS3.deleteObject(new DeleteObjectRequest(tempBucket, tempFileName));
            return amazonS3.getUrl(bucket, permanentFileName).toString();
        } catch (Exception e) {
            log.error("Error moving file to permanent storage: {}", e.getMessage(), e);
            return null;
        }
    }

}
