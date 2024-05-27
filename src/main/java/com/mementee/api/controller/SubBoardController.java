package com.mementee.api.controller;

import com.mementee.api.domain.Reply;
import com.mementee.api.domain.SubBoard;
import com.mementee.api.dto.CommonApiResponse;
import com.mementee.api.dto.PageInfo;
import com.mementee.api.dto.subBoardDTO.*;
import com.mementee.api.service.SubBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "자유 글 쓰기, 자유 글 리스트, 자유 글 조회")
public class SubBoardController {

    private final SubBoardService subBoardService;

    @Operation(description = "자유 글 쓰기 -" +
            "  {\"title\": \"string\",\n" +
            "  \"content\": \"string\"," +
            "  \"boardCategory\": \"이공\" }\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "등록 성공"),
            @ApiResponse(responseCode = "fail", description = "등록 실패")})
    @PostMapping(value = "/api/subBoard", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public CommonApiResponse<?> saveFreeSubBoard(@RequestBody @Valid WriteSubBoardRequest request, @RequestHeader("Authorization") String authorizationHeader,
                                             @RequestPart(value = "files", required = false) List<MultipartFile> multipartFiles){
            subBoardService.saveFreeSubBoard(request, authorizationHeader, multipartFiles);
            return CommonApiResponse.createSuccess();
    }

    @Operation(description = "요청 글 쓰기 -" +
            "  {\"title\": \"string\",\n" +
            "  \"content\": \"string\"," +
            "  \"boardCategory\": \"이공\"  }\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "등록 성공"),
            @ApiResponse(responseCode = "fail", description = "등록 실패")})
    @PostMapping(value = "/api/requestBoard", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public CommonApiResponse<?> saveReQuestSubBoard(@RequestBody @Valid WriteSubBoardRequest request, @RequestHeader("Authorization") String authorizationHeader,
                                             @RequestPart(value = "files", required = false) List<MultipartFile> multipartFiles){
        subBoardService.saveRequestSubBoard(request, authorizationHeader, multipartFiles);
        return CommonApiResponse.createSuccess();
    }

    @Operation(description = "글 수정 -" +
            "  {\"title\": \"string\",\n" +
            "  \"content\": \"string\"," +
            "  \"boardCategory\": \"이공\" }\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail", description = "실패")})
    @PutMapping(value = "/api/subBoard/{subBoardId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public CommonApiResponse<?> modifySubBoard(@RequestBody @Valid WriteSubBoardRequest request, @RequestHeader("Authorization") String authorizationHeader,
                                               @RequestPart(value = "files", required = false) List<MultipartFile> multipartFiles,
                                               @PathVariable Long subBoardId){
        subBoardService.modifySubBoard(request, authorizationHeader, multipartFiles, subBoardId);
        return CommonApiResponse.createSuccess();
    }

    //Page 자유 게시판 글 전체 조회 --------------
    @Operation(description = "페이지 단위로 자유 게시판 (page 사용)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/pageSubBoards")
    public CommonApiResponse<PaginationSubBoardResponse> pageBoardsList(@RequestParam int page, @RequestParam int size,
                                                                     @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)

        Page<SubBoard> findSubBoards = subBoardService.findAllFreeSubBoard(pageable);
        PageInfo pageInfo = new PageInfo(page, size, (int) findSubBoards.getTotalElements(), findSubBoards.getTotalPages());
        List<SubBoard> response = findSubBoards.getContent();
        List<SubBoardDTO> list = subBoardService.createSubBoardDTOs(response, authorizationHeader);
        return CommonApiResponse.createSuccess(new PaginationSubBoardResponse(list, pageInfo));
    }

    @Operation(description = "페이지 단위로 요청 게시판 (page 사용)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/requestSubBoards")
    public CommonApiResponse<PaginationSubBoardResponse> pageRequestBoardsList(@RequestParam int page, @RequestParam int size,
                                                                               @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)

        Page<SubBoard> findSubBoards = subBoardService.findAllRequestSubBoard(pageable);
        PageInfo pageInfo = new PageInfo(page, size, (int) findSubBoards.getTotalElements(), findSubBoards.getTotalPages());
        List<SubBoard> response = findSubBoards.getContent();
        List<SubBoardDTO> list = subBoardService.createSubBoardDTOs(response, authorizationHeader);
        return CommonApiResponse.createSuccess(new PaginationSubBoardResponse(list, pageInfo));
    }

    //글 조회
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "글 조회 성공"),
            @ApiResponse(responseCode = "fail", description = "글 조회 실패")})
    @GetMapping("/api/subBoard/{subBoardId}")
    public CommonApiResponse<SubBoardInfoResponse> subBoardInfo(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                             @PathVariable Long subBoardId) {
            SubBoardInfoResponse response = subBoardService.createSubBoardInfoResponse(subBoardId, authorizationHeader);
            return CommonApiResponse.createSuccess(response);
    }

    //댓글 기능
    @Operation(description = "댓글 달기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail", description = "실패")})
    @PostMapping("/api/reply/{subBoardId}")
    public CommonApiResponse<?> saveReply(@RequestBody ReplyRequest request, @RequestHeader("Authorization") String authorizationHeader,
                                            @PathVariable Long subBoardId) {
            subBoardService.saveReply(request, subBoardId, authorizationHeader);
            return CommonApiResponse.createSuccess();
    }

    //댓글 수정
    @Operation(description = "댓글 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail", description = "실패")})
    @PutMapping("/api/reply/{replyId}")
    public CommonApiResponse<?> modifyReply(@RequestBody ReplyRequest request, @RequestHeader("Authorization") String authorizationHeader,
                                                 @PathVariable Long replyId) {
        subBoardService.modifyReply(request, replyId, authorizationHeader);
        return CommonApiResponse.createSuccess();
    }

    //댓글 삭제
    @Operation(description = "댓글 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail", description = "실패")})
    @DeleteMapping("/api/reply/{replyId}")
    public CommonApiResponse<?> deleteReply(@RequestHeader("Authorization") String authorizationHeader,
                                              @PathVariable Long replyId) {
        subBoardService.removeReply(replyId, authorizationHeader);
        return CommonApiResponse.createSuccess();
    }

    //댓글 목록
    @Operation(description = "페이지 단위로 댓글보기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/reply/{subBoardId}")
    public CommonApiResponse<PaginationReplyResponse> pageReply(@RequestParam int page, @RequestParam int size,
                                                             @PathVariable Long subBoardId) {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id")); //내림차 순(최신순)
            Page<Reply> findReplies = subBoardService.findAllReply(subBoardId, pageable);
            PageInfo pageInfo = new PageInfo(page, size, (int) findReplies.getTotalElements(), findReplies.getTotalPages());
            List<ReplyDTO> list = ReplyDTO.createReplyDTOs(findReplies.getContent());
            return CommonApiResponse.createSuccess(new PaginationReplyResponse(list, pageInfo));
    }

    //좋아요 누르기
    @Operation(description = "좋아요 누르기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail", description = "실패")})
    @PostMapping("/api/like/{subBoardId}")
    public CommonApiResponse<?> addLikeCount(@RequestHeader("Authorization") String authorizationHeader,
                                               @PathVariable Long subBoardId) {
            subBoardService.addSubBoardLike(subBoardId, authorizationHeader);
            return CommonApiResponse.createSuccess();
    }

    //좋아요 취소
    @Operation(description = "좋아요 취소")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "즐겨찾기 삭제 성공"),
            @ApiResponse(responseCode = "fail", description = "즐겨찾기 삭제 실패")})
    @DeleteMapping("/api/like/{subBoardId}")
    public CommonApiResponse<?> removeFavorite(@RequestHeader("Authorization") String authorizationHeader,
                                                 @PathVariable Long subBoardId){
            subBoardService.removeSubBoardLike(subBoardId, authorizationHeader);
            return CommonApiResponse.createSuccess();
    }
}

