package com.mementee.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.UnsupportedMediaTypeException;

import java.io.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.s3.bucket_image}")
    private String bucket_image;
    @Value("${cloud.aws.s3.bucket_video}")
    private String bucket_video;
    @Value("${cloud.aws.s3.bucket_pdf}")
    private String bucket_pdf;



    public String saveFile(MultipartFile multipartFile) throws IOException {

        String originalFilename = multipartFile.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        //파일을 S3 버킷에 저장, 파일이름, InputStream, metadata
        amazonS3.putObject(bucket, originalFilename, multipartFile.getInputStream(), metadata);
        return amazonS3.getUrl(bucket, originalFilename).toString();
    }

    //originalFilename 로 조회
    public String getImageUrl(String imageName) {
        try {
            S3Object s3Object = amazonS3.getObject(bucket, imageName);

            if (s3Object != null) {
                // 이미지의 S3 URL을 반환
                return amazonS3.getUrl(bucket, imageName).toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public String saveImage(MultipartFile file) {
        try {
            ByteArrayOutputStream compressedImageStream = compressImage(file);
            byte[] imageBytes = compressedImageStream.toByteArray();

            // Generate a random file name with the appropriate extension
            String extension = "jpg"; // Default to jpg if compression is applied
            String imageName = UUID.randomUUID() + "." + extension;

            // Prepare the metadata for S3 object
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imageBytes.length);
            metadata.setContentType("image/jpeg");

            // Upload the image to S3
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
            amazonS3.putObject(new PutObjectRequest(bucket_image, imageName, inputStream, metadata));

            // Return the URL of the uploaded image
            return amazonS3.getUrl(bucket_image, imageName).toString();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    public ByteArrayOutputStream compressImage(MultipartFile file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Compress the image if it is larger than 2MB
        if (file.getSize() > 1024 * 1024 * 2) {
            Thumbnails.of(file.getInputStream())
                    .size(1280, 720)
                    .outputFormat("jpg")
                    .outputQuality(0.75)
                    .toOutputStream(outputStream);
        } else {
            file.getInputStream().transferTo(outputStream);
        }

        return outputStream;
    }

    public String save(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            String contentType = file.getContentType();

            String s3FileName = UUID.randomUUID() + "." + extension;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(contentType);

            String bucketName = "fiveidiots-" + extractBucketName(contentType);

            amazonS3.putObject(new PutObjectRequest(bucketName, s3FileName, file.getInputStream(), metadata));

            return amazonS3.getUrl(bucketName, s3FileName).toString();
        } catch (IOException e) {
            return "Error uploading file: " + e.getMessage();
        }
    }

    public String extractBucketName(String str) {
        if (str.startsWith("image")) return "image";
        if (str.startsWith("video")) return "video";
        if (str.equals("application/pdf")) return "pdf";
        if (str.equals("application/zip")) return "zip";
        if (str.equals("text/vcard")) return "vcard";

        throw new UnsupportedMediaTypeException("Unsupported file type.");
    }
}
