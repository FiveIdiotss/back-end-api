package com.mementee.api.controller;

import com.mementee.api.dto.CommonApiResponse;
import com.mementee.s3.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "이미지 URL")
public class ImageUrlController {

    private final S3Service s3Service;

    @Operation(summary = "이미지 url 리턴", description = "이미지 첨부기능에 대한 이미지 url return")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "등록 성공"),
            @ApiResponse(responseCode = "fail", description = "등록 실패")})
    @PostMapping(value = "/api/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonApiResponse<String> saveImage(@RequestPart MultipartFile multipartFile){
//        String tempUrl = s3Service.saveFileToTemp(multipartFile);
        String tempUrl = s3Service.save(multipartFile);
        return CommonApiResponse.createSuccess(tempUrl);
    }
}
