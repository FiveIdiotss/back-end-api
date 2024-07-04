package com.team.mementee.api.controller;

import com.team.mementee.api.domain.enumtype.BoardCategory;
import com.team.mementee.api.dto.CommonApiResponse;
import com.team.mementee.api.dto.PageInfo;
import com.team.mementee.api.dto.applyDTO.ApplyRequest;
import com.mementee.api.dto.boardDTO.*;
import com.team.mementee.api.domain.Board;
import com.team.mementee.api.dto.boardDTO.BoardDTO;
import com.team.mementee.api.dto.boardDTO.BoardInfoResponse;
import com.team.mementee.api.dto.boardDTO.PaginationBoardResponse;
import com.team.mementee.api.dto.boardDTO.WriteBoardRequest;
import com.team.mementee.api.dto.notificationDTO.FcmDTO;
import com.team.mementee.api.service.ApplyService;
import com.team.mementee.api.service.FcmService;
import com.team.mementee.api.service.NotificationService;
import com.team.mementee.api.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "글 쓰기, 글 리스트, 글 조회")
public class BoardController {

    private final NotificationService notificationService;
    private final BoardService boardService;
    private final ApplyService applyService;
    private final FcmService fcmService;
    private final RedisTemplate<String, String> redisTemplate;

    @Operation(summary = "글 쓰기 , 스웨거용 ", description =
            "  {\"title\": \"축구 교실\",\n" +
            "  \"introduce\": \"맨유 출신 입니다.\",\n" +
            "  \"target\": \"세모발들\",\n" +
            "  \"content\": \"맨유출신한테 축구배우실분 모집합니다.\",\n" +
            "  \"consultTime\": 30,\n" +
            "  \"boardCategory\": \"예체능\",\n" +
            "  \"times\": [\n" +
            "    {  \"startTime\": \"09:00:00\",\n" +
            "      \"endTime\": \"12:00:00\" },\n" +
            "    {  \"startTime\": \"14:00:00\",\n" +
            "      \"endTime\": \"17:00:00\" }\n" +
            "  ],\n" +
            "  \"availableDays\": [\n" +
            "    \"MONDAY \",\n" +
            "    \"SUNDAY\"\n" +
            "  ]\n" +
            "}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "등록 성공"),
            @ApiResponse(responseCode = "fail", description = "등록 실패")})
    @PostMapping(value = "/api/swagger/board")
    public CommonApiResponse<?> saveSwaggerBoard(@RequestBody @Valid WriteBoardRequest request,
                                                 @RequestHeader("Authorization") String authorizationHeader){
        List<MultipartFile> multipartFiles = new ArrayList<>();
        boardService.saveBoard(request, multipartFiles, authorizationHeader);
        return CommonApiResponse.createSuccess();
    }

    @Operation(summary = "글쓰기 스웨거 에서 x", description =
            "  {\"title\": \"축구 교실\",\n" +
                    "  \"introduce\": \"맨유 출신 입니다.\",\n" +
                    "  \"target\": \"세모발들\",\n" +
                    "  \"content\": \"맨유출신한테 축구배우실분 모집합니다.\",\n" +
                    "  \"consultTime\": 30,\n" +
                    "  \"boardCategory\": \"예체능\",\n" +
                    "  \"times\": [\n" +
                    "    {  \"startTime\": \"09:00:00\",\n" +
                    "      \"endTime\": \"12:00:00\" },\n" +
                    "    {  \"startTime\": \"14:00:00\",\n" +
                    "      \"endTime\": \"17:00:00\" }\n" +
                    "  ],\n" +
                    "  \"availableDays\": [\n" +
                    "    \"MONDAY \",\n" +
                    "    \"SUNDAY\"\n" +
                    "  ]\n" +
                    "}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "등록 성공"),
            @ApiResponse(responseCode = "fail", description = "등록 실패")})
    @PostMapping(value = "/api/board")
    public CommonApiResponse<?> saveBoard(@RequestHeader("Authorization") String authorizationHeader,
                                          @RequestPart @Valid WriteBoardRequest request,
                                          @RequestPart(value = "images", required = false) List<MultipartFile> multipartFiles) {
        boardService.saveBoard(request, multipartFiles, authorizationHeader);
        return CommonApiResponse.createSuccess();
    }

    @Operation(summary = "글 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "글 조회 성공"),
            @ApiResponse(responseCode = "fail", description = "글 조회 실패")})
    @GetMapping("/api/board/{boardId}")
    public CommonApiResponse<BoardInfoResponse> boardInfo(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                          @PathVariable Long boardId){
            BoardInfoResponse response = boardService.createBoardInfoResponse(boardId, authorizationHeader);
            return CommonApiResponse.createSuccess(response);
    }

    @Operation(summary = "게시물 수정", description = "상담가능 시간 또는 요일 수정 시 이미 신청되있던 사람에 대하여 예외 발생 시켜야함 (ex- 바뀌기 전 시간에 신청한 사람이 있을경우)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @PutMapping("/api/board/{boardId}")
    public CommonApiResponse<?> boardModify(@RequestHeader("Authorization") String authorizationHeader,
                                            @RequestBody @Valid WriteBoardRequest request,
                                            @PathVariable Long boardId){
            boardService.modifyBoard(request, authorizationHeader, boardId);
            return CommonApiResponse.createSuccess();
    }

    @Operation(summary = "필터별 검색", description = "헤더 넣지 않고 RequestParam 에 아무것도 넣지 않으면 그냥 전체 게시판, " +
                                                   "헤더만 넣고 RequestParam 에 아무것도 넣지 않으면 전체 게시판이지만 즐겨찾기 된것은 true로 return, " +
                                                   "RequestParam 에 따라 필터별 검색 Page return")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "검색 성공"),
            @ApiResponse(responseCode = "fail", description = "검색 실패")})
    @GetMapping("/api/boards/filter")
    public CommonApiResponse<PaginationBoardResponse> findBoards(@RequestParam int page, @RequestParam int size,
                                                                 @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                                 @RequestParam(required = false) boolean schoolFilter,
                                                                 @RequestParam(required = false) boolean favoriteFilter,
                                                                 @RequestParam(required = false) BoardCategory boardCategory,
                                                                 @RequestParam(required = false) String keyWord) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)
        Page<Board> findBoards = boardService.findBoardsByFilter(authorizationHeader, schoolFilter, favoriteFilter, boardCategory, keyWord, pageable);
        PageInfo pageInfo = new PageInfo(page, size, (int)findBoards.getTotalElements(), findBoards.getTotalPages());

        List<Board> response = findBoards.getContent();
        List<BoardDTO> list = boardService.createBoardDTOs(response, authorizationHeader);
        return CommonApiResponse.createSuccess(new PaginationBoardResponse(list, pageInfo));
    }

    @Operation(summary = "특정 멤버가 쓴 글 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "글 리스트 조회 성공"),
            @ApiResponse(responseCode = "fail", description = "즐겨찾기 추가 실패")})
    @GetMapping("/api/memberBoards/{memberId}")
    public CommonApiResponse<PaginationBoardResponse> memberBoards(@RequestParam int page, @RequestParam int size,
                                                                   @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                                   @PathVariable("memberId") Long memberId){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)
        Page<Board> findBoards = boardService.findBoardsByMember(memberId, pageable);
        PageInfo pageInfo = new PageInfo(page, size, (int)findBoards.getTotalElements(), findBoards.getTotalPages());

        List<Board> response = findBoards.getContent();
        List<BoardDTO> list = boardService.createBoardDTOs(response, authorizationHeader);
        return CommonApiResponse.createSuccess(new PaginationBoardResponse(list, pageInfo));
    }

    @Operation(summary = "즐겨찾기 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "즐겨찾기 추가 성공"),
            @ApiResponse(responseCode = "fail", description = "즐겨찾기 추가 실패")})
    @PostMapping("/api/board/favorite/{boardId}")
    public CommonApiResponse<?> addFavorite(@RequestHeader("Authorization") String authorizationHeader,
                                            @PathVariable Long boardId){
            boardService.addFavoriteBoard(authorizationHeader, boardId);
            return CommonApiResponse.createSuccess();
    }

    @Operation(summary = "즐겨찾기 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "즐겨찾기 삭제 성공"),
            @ApiResponse(responseCode = "fail", description = "즐겨찾기 삭제 실패")})
    @DeleteMapping("/api/board/favorite/{boardId}")
    public CommonApiResponse<?> removeFavorite(@RequestHeader("Authorization") String authorizationHeader,
                                               @PathVariable Long boardId){
            boardService.removeFavoriteBoard(authorizationHeader, boardId);
            return CommonApiResponse.createSuccess();
    }

    @Operation(summary = "게시글에 멘티 신청",description = "이미 신청한 글이거나, 자신이 쓴 글에 신청시 오류" +
            "{\n" +
            "  \"content\": \"구민회 탈모\",\n" +
            "  \"date\": \"2024-02-11\",\n" +
            "  \"time\": \"09:00:00\"\n" +
            "}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "신청 성공"),
            @ApiResponse(responseCode = "fail", description = "신청 실패")})
    @PostMapping("/api/board/{boardId}")
    public CommonApiResponse<?> boardApply(@RequestBody @Valid ApplyRequest request, @PathVariable Long boardId,
                                           @RequestHeader("Authorization") String authorizationHeader){
        applyService.sendApply(authorizationHeader, boardId, request);
        FcmDTO fcmDTO = fcmService.createApplyFcmDTO(authorizationHeader, boardId, request);
        fcmService.sendMessageTo(fcmDTO);
        notificationService.saveNotification(fcmDTO);
        return CommonApiResponse.createSuccess();
    }

    // 인기 키워드 Top 5 조회
    @Operation(summary = "멘토 게시판에 대한 인기 키워드 top 9")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "조회 성공"),
            @ApiResponse(responseCode = "fail", description = "조회 실패")})
    @GetMapping("/api/popular-keywords")
    public CommonApiResponse<ArrayList<String>> getPopularKeywords() {
        // Redis에서 인기 키워드 Top 5 조회
        Set<String> popularKeywords = redisTemplate.opsForZSet().reverseRange("popular_keywords", 0, 9);
        // 결과 반환;
        return CommonApiResponse.createSuccess(new ArrayList<>(popularKeywords));
    }
}
