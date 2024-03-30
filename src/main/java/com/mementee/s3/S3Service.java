package com.mementee.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.additional}")
    private String bucket2;


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

    public String saveChatImage(String base64ImageData, String imageName) throws IOException {
        // base64 데이터 파싱
        String[] split = base64ImageData.split(",");
        String base64Image = split[1];

        // 이미지 확장자 추출
        String extension;
        if (split[0].equals("data:image/jpeg;base64")) {
            extension = "jpeg";
        } else if (split[0].equals("data:image/png;base64")) {
            extension = "png";
        } else {
            extension = "jpg";
        }

        // base64 문자열을 바이트 배열로 변환
        byte[] imageBytes = DatatypeConverter.parseBase64Binary(base64Image);

        // S3에 업로드할 파일명 설정

        // 업로드할 이미지 데이터 설정
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageBytes.length);
        metadata.setContentType("image/" + extension);

        // S3에 이미지 업로드
        amazonS3.putObject(new PutObjectRequest(bucket, imageName, inputStream, metadata));

        // 업로드된 이미지의 URL 반환
        return amazonS3.getUrl(bucket, imageName).toString();
    }

}