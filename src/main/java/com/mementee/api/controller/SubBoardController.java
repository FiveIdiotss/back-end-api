package com.mementee.api.controller;

import com.mementee.api.domain.Member;
import com.mementee.api.domain.Reply;
import com.mementee.api.domain.SubBoard;
import com.mementee.api.domain.enumtype.BoardCategory;
import com.mementee.api.domain.enumtype.SubBoardType;
import com.mementee.api.dto.CommonApiResponse;
import com.mementee.api.dto.PageInfo;
import com.mementee.api.dto.notificationDTO.FcmDTO;
import com.mementee.api.dto.subBoardDTO.*;
import com.mementee.api.service.FcmService;
import com.mementee.api.service.MemberService;
import com.mementee.api.service.NotificationService;
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
    private final FcmService fcmService;
    private final NotificationService notificationService;
    private final MemberService memberService;
    @Operation(summary = "질문 글쓰기 / 요청과 통합 예정", description =
            "  {\"title\": \"이거 아시는분\",\n" +
            "  \"content\": \"1+1 이 뭔가요?\"," +
            "  \"boardCategory\": \"이공\" }\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "등록 성공"),
            @ApiResponse(responseCode = "fail", description = "등록 실패")})
    @PostMapping(value = "/api/subBoard", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public CommonApiResponse<?> saveQuestSubBoard(@RequestBody @Valid WriteSubBoardRequest request, @RequestHeader("Authorization") String authorizationHeader,
                                             @RequestPart(value = "files", required = false) List<MultipartFile> multipartFiles){
            subBoardService.saveFreeSubBoard(request, authorizationHeader, multipartFiles);
            return CommonApiResponse.createSuccess();
    }

    @Operation(summary = "멘토 요청 글쓰기 / 질문과 통합 예정", description =
            "  {\"title\": \"c언어 강의 알려주실분\",\n" +
            "  \"content\": \"포인터좀 알려주세요\"," +
            "  \"boardCategory\": \"이공\"  }\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "등록 성공"),
            @ApiResponse(responseCode = "fail", description = "등록 실패")})
    @PostMapping(value = "/api/requestBoard", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public CommonApiResponse<?> saveRequestSubBoard(@RequestBody @Valid WriteSubBoardRequest request, @RequestHeader("Authorization") String authorizationHeader,
                                                    @RequestPart(value = "files", required = false) List<MultipartFile> multipartFiles){
        subBoardService.saveRequestSubBoard(request, authorizationHeader, multipartFiles);
        return CommonApiResponse.createSuccess();
    }

    @Operation(summary = "글 수정", description =
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

    //글 조회
    @Operation(summary = "질문/요청글 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "글 조회 성공"),
            @ApiResponse(responseCode = "fail", description = "글 조회 실패")})
    @GetMapping("/api/subBoard/{subBoardId}")
    public CommonApiResponse<SubBoardInfoResponse> findSubBoard(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                             @PathVariable Long subBoardId) {
            SubBoardInfoResponse response = subBoardService.createSubBoardInfoResponse(subBoardId, authorizationHeader);
            return CommonApiResponse.createSuccess(response);
    }

    //댓글 기능
    @Operation(summary = "질문/요청글에 댓글 달기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail", description = "실패")})
    @PostMapping("/api/reply/{subBoardId}")
    public CommonApiResponse<?> saveReply(@RequestBody ReplyRequest request, @RequestHeader("Authorization") String authorizationHeader,
                                          @PathVariable Long subBoardId) {
        SubBoard subBoard = subBoardService.findSubBoardById(subBoardId);
        Member member = memberService.findMemberByToken(authorizationHeader);
        subBoardService.saveReply(request, subBoard, member);

        if(subBoard.getMember().equals(member))    //자신의 글에 자신이 댓글 쓸 때
            return CommonApiResponse.createSuccess();

        FcmDTO fcmDTO = fcmService.createReplyFcmDTO(authorizationHeader, subBoard, request);
        fcmService.sendMessageTo(fcmDTO);
        notificationService.saveNotification(fcmDTO);
        return CommonApiResponse.createSuccess();
    }

    //댓글 수정
    @Operation(summary = "댓글 수정")
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
    @Operation(summary = "댓글 삭제")
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
    @Operation(summary = "페이지 단위로 댓글 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/reply/{subBoardId}")
    public CommonApiResponse<PaginationReplyResponse> findReplies(@RequestParam int page, @RequestParam int size,
                                                                  @RequestParam boolean isRecent,
                                                                  @PathVariable Long subBoardId) {
        // 정렬 방향 결정
        Sort sort = isRecent ? Sort.by("id").descending() : Sort.by("id").ascending();

        Page<Reply> findReplies = subBoardService.findAllReply(subBoardId, PageRequest.of(page - 1, size, sort));
        PageInfo pageInfo = new PageInfo(page, size, (int) findReplies.getTotalElements(), findReplies.getTotalPages());
        List<ReplyDTO> list = ReplyDTO.createReplyDTOs(findReplies.getContent());
        return CommonApiResponse.createSuccess(new PaginationReplyResponse(list, pageInfo));
    }

    //좋아요 누르기
    @Operation(summary = "좋아요")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail", description = "실패")})
    @PostMapping("/api/like/{subBoardId}")
    public CommonApiResponse<?> addLike(@RequestHeader("Authorization") String authorizationHeader,
                                        @PathVariable Long subBoardId) {
            subBoardService.addSubBoardLike(subBoardId, authorizationHeader);
            return CommonApiResponse.createSuccess();
    }

    //좋아요 취소
    @Operation(summary = "좋아요 취소")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "즐겨찾기 삭제 성공"),
            @ApiResponse(responseCode = "fail", description = "즐겨찾기 삭제 실패")})
    @DeleteMapping("/api/like/{subBoardId}")
    public CommonApiResponse<?> removeLike(@RequestHeader("Authorization") String authorizationHeader,
                                                 @PathVariable Long subBoardId){
            subBoardService.removeSubBoardLike(subBoardId, authorizationHeader);
            return CommonApiResponse.createSuccess();
    }

    //Page 질문/요청 게시판 글 전체 조회 --------------
    @Operation(summary = "질문 게시판 목록(삭제 예정)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/pageSubBoards")
    public CommonApiResponse<PaginationSubBoardResponse> findAll(@RequestParam int page, @RequestParam int size,
                                                                 @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)

        Page<SubBoard> findSubBoards = subBoardService.findAllFreeSubBoard(pageable);
        PageInfo pageInfo = new PageInfo(page, size, (int) findSubBoards.getTotalElements(), findSubBoards.getTotalPages());
        List<SubBoard> response = findSubBoards.getContent();
        List<SubBoardDTO> list = subBoardService.createSubBoardDTOs(response, authorizationHeader);
        return CommonApiResponse.createSuccess(new PaginationSubBoardResponse(list, pageInfo));
    }

    @Operation(summary = "필터별 멘토 질문/요청 게시판 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/subBoards")
    public CommonApiResponse<PaginationSubBoardResponse> findSubBoards(@RequestParam int page, @RequestParam int size,
                                                                       @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                                       @RequestParam(required = false) boolean schoolFilter,
                                                                       @RequestParam(required = false) boolean favoriteFilter,
                                                                       @RequestParam(required = false) BoardCategory boardCategory,
                                                                       @RequestParam(required = false) String keyWord,
                                                                       @RequestParam SubBoardType subBoardType) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)

        Page<SubBoard> findSubBoards = subBoardService.findSubBoardsByFilter(authorizationHeader, schoolFilter, favoriteFilter, boardCategory, keyWord, subBoardType, pageable);
        PageInfo pageInfo = new PageInfo(page, size, (int) findSubBoards.getTotalElements(), findSubBoards.getTotalPages());
        List<SubBoard> response = findSubBoards.getContent();
        List<SubBoardDTO> list = subBoardService.createSubBoardDTOs(response, authorizationHeader);
        return CommonApiResponse.createSuccess(new PaginationSubBoardResponse(list, pageInfo));
    }

    //특정 멤버가 쓴 글 목록
    @Operation(summary = "특정 멤버가 쓴 질문/요청 글 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "글 리스트 조회 성공"),
            @ApiResponse(responseCode = "fail", description = "즐겨찾기 추가 실패")})
    @GetMapping("/api/subBoards/{memberId}")
    public CommonApiResponse<PaginationSubBoardResponse> memberBoards(@RequestParam int page, @RequestParam int size,
                                                                      @RequestParam SubBoardType subBoardType,
                                                                      @PathVariable("memberId") Long memberId,
                                                                      @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)
        Page<SubBoard> findSubBoards = subBoardService.findSubBoardsBySubBoardTypeAndMember(subBoardType, memberId, pageable);
        PageInfo pageInfo = new PageInfo(page, size, (int) findSubBoards.getTotalElements(), findSubBoards.getTotalPages());

        List<SubBoard> response = findSubBoards.getContent();
        List<SubBoardDTO> list = subBoardService.createSubBoardDTOs(response, authorizationHeader);
        return CommonApiResponse.createSuccess(new PaginationSubBoardResponse(list, pageInfo));
    }
}

