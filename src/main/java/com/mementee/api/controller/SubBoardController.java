package com.mementee.api.controller;

import com.mementee.api.domain.Reply;
import com.mementee.api.domain.SubBoard;
import com.mementee.api.dto.boardDTO.*;
import com.mementee.api.dto.subBoardDTO.*;
import com.mementee.api.service.MemberService;
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
    private final MemberService memberService;

    @Operation(description = "글 쓰기 -" +
            "  {\"title\": \"string\",\n" +
            "  \"content\": \"string\" }\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "등록 성공"),
            @ApiResponse(responseCode = "fail", description = "등록 실패")})
    @PostMapping(value = "/api/subBoard", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> saveSubBoard(@RequestBody @Valid WriteSubBoardRequest request, @RequestHeader("Authorization") String authorizationHeader,
                                               @RequestPart(value = "files", required = false) List<MultipartFile> multipartFiles) {
        try {
            subBoardService.saveSubBoard(request, multipartFiles, authorizationHeader);
            return ResponseEntity.ok().body("글 등록 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //Page 자유 게시판 글 전체 조회 --------------
    @Operation(description = "페이지 단위로 자유 게시판 (page 사용)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/pageSubBoards")
    public ResponseEntity<PaginationSubBoardResponseDto> pageBoardsList(@RequestParam int page, @RequestParam int size,
                                                                        @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)

        Page<SubBoard> findSubBoards = subBoardService.findAllByBoardTypeByPage(pageable);
        PageInfo pageInfo = new PageInfo(page, size, (int) findSubBoards.getTotalElements(), findSubBoards.getTotalPages());

        List<SubBoard> response = findSubBoards.getContent();
        List<SubBoardDTO> list = subBoardService.createSubBoardDTO(response, authorizationHeader);
        return new ResponseEntity<>(new PaginationSubBoardResponseDto(list, pageInfo), HttpStatus.OK);
    }

    //글 조회
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "글 조회 성공"),
            @ApiResponse(responseCode = "fail", description = "글 조회 실패")})
    @GetMapping("/api/subBoard/{subBoardId}")
    public ResponseEntity<?> subBoardInfo(@PathVariable Long subBoardId,
                                          @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            SubBoard subBoard = subBoardService.findSubBoard(subBoardId);
            SubBoardInfoResponse response = subBoardService.createSubBoardInfoResponse(subBoard, authorizationHeader);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("글 조회 실패");
        }
    }

    //댓글 기능
    @Operation(description = "댓글")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail", description = "실패")})
    @PostMapping("/api/reply/{subBoardId}")
    public ResponseEntity<String> saveReply(@RequestBody ReplyRequest request, @RequestHeader("Authorization") String authorizationHeader,
                                            @PathVariable Long subBoardId) {
        try {
            subBoardService.saveReply(request, subBoardId, authorizationHeader);
            return ResponseEntity.ok("댓글 작성 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("댓글작성 실패");
        }
    }

    @Operation(description = "페이지 단위로 댓글보기 (page 사용)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/reply/{subBoardId}")
    public ResponseEntity<?> pageReply(@RequestParam int page, @RequestParam int size,
                                       @PathVariable Long subBoardId) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id")); //내림차 순(최신순)

            Page<Reply> findReplies = subBoardService.findAllReply(subBoardId, pageable);
            PageInfo pageInfo = new PageInfo(page, size, (int) findReplies.getTotalElements(), findReplies.getTotalPages());

            List<Reply> response = findReplies.getContent();
            List<ReplyDTO> list = response.stream().map
                            (r -> new ReplyDTO(r.getId(), r.getMember().getId(), r.getMember().getMemberImageUrl(),
                                    r.getMember().getName(), r.getMember().getMajor().getName(), r.getWriteTime(), r.getContent()))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(new PaginationReplyResponseDto(list, pageInfo), HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("조회 실패");
        }
    }

    //좋아요 누르기
    @Operation(description = "좋아요 누르기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail", description = "실패")})
    @PostMapping("/api/like/{subBoardId}")
    public ResponseEntity<String> addLikeCount(@RequestHeader("Authorization") String authorizationHeader,
                                               @PathVariable Long subBoardId) {
        try {
            subBoardService.addSubBoardLike(subBoardId, authorizationHeader);
            return ResponseEntity.ok("좋아요 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("댓글작성 실패");
        }
    }

    //좋아요 취소
    @Operation(description = "좋아요 취소")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "즐겨찾기 삭제 성공"),
            @ApiResponse(responseCode = "fail", description = "즐겨찾기 삭제 실패")})
    @DeleteMapping("/api/like/{subBoardId}")
    public ResponseEntity<String> removeFavorite(@RequestHeader("Authorization") String authorizationHeader,
                                                 @PathVariable Long subBoardId){
        try {
            subBoardService.removeSubBoardLike(subBoardId, authorizationHeader);
            return ResponseEntity.ok("좋아요 취소 성공");
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}

