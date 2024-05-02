package com.mementee.api.controller;

import com.mementee.api.domain.SubBoard;
import com.mementee.api.domain.SubBoardImage;
import com.mementee.api.dto.boardDTO.*;
import com.mementee.api.dto.subBoardDTO.*;
import com.mementee.api.service.SubBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "자유 글 쓰기, 자유 글 리스트, 자유 글 조회")
public class SubBoardController {

    private final SubBoardService subBoardService;


    @Operation(description = "글 쓰기 -" +
            "  {\"title\": \"string\",\n" +
            "  \"content\": \"string\" }\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "등록 성공"),
            @ApiResponse(responseCode = "fail", description = "등록 실패")})
    @PostMapping(value = "/api/subBoard", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> saveSubBoard(@RequestBody @Valid WriteSubBoardRequest request, @RequestHeader("Authorization") String authorizationHeader,
                                               @RequestPart(value = "files", required = false) List<MultipartFile> multipartFiles){
        try {
            subBoardService.saveSubBoard(request, multipartFiles, authorizationHeader);
            return ResponseEntity.ok().body("글 등록 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //Page 자유 게시판 글 전체 조회 --------------
    @Operation(description =  "페이지 단위로 자유 게시판 (page 사용)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/pageSubBoards")
    public ResponseEntity pageBoardsList(@RequestParam int page, @RequestParam int size){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)

        Page<SubBoard> findSubBoards = subBoardService.findAllByBoardTypeByPage(pageable);
        PageInfo pageInfo = new PageInfo(page, size, (int)findSubBoards.getTotalElements(), findSubBoards.getTotalPages());

        List<SubBoard> response = findSubBoards.getContent();
        List<SubBoardDTO> list = response.stream().map
                        (b -> new SubBoardDTO(b.getId(), b.getTitle(),b.getContent(),
                                b.getMember().getYear(), b.getMember().getSchool().getName(), b.getMember().getMajor().getName(),
                                b.getMember().getId(), b.getMember().getName(), b.getWriteTime()))
                .collect(Collectors.toList());

        return new ResponseEntity<>(new PaginationSubBoardResponseDto(list, pageInfo), HttpStatus.OK);
    }

    @Operation(description = "페이지 단위로 자유 게시판 학교별 리스트 (page 사용)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/pageSubBoards/{schoolName}")
    public ResponseEntity pageBoardListBySchoolName(@RequestParam int page, @RequestParam int size,
                                                    @PathVariable String schoolName){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)

        Page<SubBoard> findSubBoards = subBoardService.findAllByBoardTypeAndSchoolNameByPage(schoolName, pageable);
        PageInfo pageInfo = new PageInfo(page, size, (int)findSubBoards.getTotalElements(), findSubBoards.getTotalPages());

        List<SubBoard> response = findSubBoards.getContent();
        List<SubBoardDTO> list = response.stream().map
                        (b -> new SubBoardDTO(b.getId(), b.getTitle(),b.getContent(),
                                b.getMember().getYear(), b.getMember().getSchool().getName(), b.getMember().getMajor().getName(),
                                b.getMember().getId(), b.getMember().getName(), b.getWriteTime()))
                .collect(Collectors.toList());

        return new ResponseEntity<>(new PaginationSubBoardResponseDto(list, pageInfo), HttpStatus.OK);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "글 조회 성공"),
            @ApiResponse(responseCode = "fail", description = "글 조회 실패")})
    @GetMapping("/api/subBoard/{subBoardId}")
    public ResponseEntity subBoardInfo(@PathVariable Long subBoardId){
        try {
            SubBoard subBoard = subBoardService.findSubBoard(subBoardId);
            List<SubBoardImage> subBoardImages = subBoardService.getSubBoardImages(subBoardId);

            List<SubBoardImageDTO> subBoardImageDTOS = subBoardImages.stream().
                    map(b -> new SubBoardImageDTO(b.getSubBoardImageUrl()))
                    .toList();

            SubBoardDTO subBoardDTO = new SubBoardDTO(subBoard.getId(),subBoard.getTitle(), subBoard.getContent(),
                    subBoard.getMember().getYear(), subBoard.getMember().getSchool().getName(), subBoard.getMember().getMajor().getName(),
                    subBoard.getMember().getId(), subBoard.getMember().getName(), subBoard.getWriteTime());

            SubBoardInfoResponse response = new SubBoardInfoResponse(subBoardDTO, subBoardImageDTOS);
            return ResponseEntity.ok(response);
        }catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("글 조회 실패");
        }
    }
}

