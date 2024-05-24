package com.mementee.api.controller;

import com.mementee.api.domain.Member;
import com.mementee.api.domain.enumtype.BoardCategory;
import com.mementee.api.dto.PageInfo;
import com.mementee.api.dto.applyDTO.ApplyRequest;
import com.mementee.api.dto.boardDTO.*;
import com.mementee.api.domain.Board;
import com.mementee.api.dto.notificationDTO.FcmDTO;
import com.mementee.api.service.ApplyService;
import com.mementee.api.service.FcmNotificationService;
import com.mementee.api.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.mementee.api.service.BoardService;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "글 쓰기, 글 리스트, 글 조회")
public class BoardController {

    private final BoardService boardService;
    private final ApplyService applicationService;
    private final NotificationService notificationService;
    private final FcmNotificationService fcmNotificationService;

    //Slice 글 리스트로 전체 조회---------------
    //멘토,멘티 글 전체 조회
//    @Operation(description =  "페이지 단위로 멘토/멘티 전체 리스트")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "success", description = "성공"),
//            @ApiResponse(responseCode = "fail")})
//    @GetMapping("/api/boards")
//    public Slice<BoardDTO> boardList(@RequestParam int page, @RequestParam int size){
//        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)
//
//        Slice<Board> findBoards = boardService.findAllByBoardType(pageable);
//        Slice<BoardDTO> slice = findBoards.map(b -> new BoardDTO(b.getId(), b.getBoardCategory(), b.getTitle(), b.getIntroduce(), b.getTarget(),b.getContent(),
//                b.getMember().getYear(), b.getMember().getSchool().getName(), b.getMember().getMajor().getName(), b.getMember().getId(), b.getMember().getName(), b.getWriteTime()));
//
//        return slice;
//    }

    //글 쓰기--------------------------------------
    @Operation(description = "글 쓰기 - 글 작성시 상담 가능한 요일들, 상담 가능  같이 적으셈" +
            "  {\"title\": \"string\",\n" +
            "  \"introduce\": \"string\",\n" +
            "  \"target\": \"string\",\n" +
            "  \"content\": \"string\",\n" +
            "  \"consultTime\": 30,\n" +
            "  \"boardCategory\": \"이공\",\n" +
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
            "}\n 이런식으로 입력 바람")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "등록 성공"),
            @ApiResponse(responseCode = "fail", description = "등록 실패")})
    @PostMapping(value = "/api/board", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> saveBoard(@RequestBody @Valid WriteBoardRequest request, @RequestHeader("Authorization") String authorizationHeader,
                                            @RequestPart(value = "files", required = false) List<MultipartFile> multipartFiles) throws IOException {
            boardService.saveBoard(request, multipartFiles, authorizationHeader);
            return ResponseEntity.ok().body("글 등록 성공");
    }

    //게시글 조회 --------------------
    @Operation(description = "글 조회" +
            "private BoardDTO boardDTO;" +
            "    private int consultTime;                //상담 시간\n" +
            "    private List<ScheduleTime> times;       //예약 가능 시간\n" +
            "    private List<DayOfWeek> availableDays;  //상담 가능한 요일" +
            "    private List<UnavailableTime> unavailableTimes; //예약된 시간들" +
            " 이 BoardInfoResponse를 받아야함" +
            "각 클래스들 --------------------------------------------------> " +
            " public class ScheduleTime {\n" +
            "    private LocalTime startTime;\n" +
            "    private LocalTime endTime; }" +
            "public class UnavailableTime {\n" +
            "    private LocalDate date;         //상담 날짜\n" +
            "    private LocalTime startTime;    //상담 시작 시간}" )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "글 조회 성공"),
            @ApiResponse(responseCode = "fail", description = "글 조회 실패")})
    @GetMapping("/api/board/{boardId}")
    public ResponseEntity<BoardInfoResponse> boardInfo(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                       @PathVariable Long boardId){
            BoardInfoResponse response = boardService.createBoardInfoResponse(boardId, authorizationHeader);
            return ResponseEntity.ok(response);
    }

    //게시물 수정 ---------------
    @Operation(description = "게시물 수정 -> 상담가능 시간 또는 요일 수정 시 이미 신청되있던 사람에 대하여 예외 발생 시켜야함 (ex- 바뀌기 전 시간에 신청한 사람이 있을경우)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @PutMapping("/api/board/{boardId}")
    public ResponseEntity<String> boardModify(@RequestBody @Valid WriteBoardRequest request, @PathVariable Long boardId,
                                              @RequestHeader("Authorization") String authorizationHeader){
            boardService.modifyBoard(request, authorizationHeader, boardId);
            return ResponseEntity.ok("수정 성공");
    }

    //Page 멘토 글 전체 조회 --------------
    @Operation(description =  "페이지 단위로 멘토 전체 리스트 (page 사용)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/pageBoards")
    public ResponseEntity<PaginationBoardResponse> pageBoardsList(@RequestParam int page, @RequestParam int size,
                                                                  @RequestHeader(value = "Authorization", required = false) String authorizationHeader){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)

        Page<Board> findBoards = boardService.findAllByPage(pageable);
        PageInfo pageInfo = new PageInfo(page, size, (int)findBoards.getTotalElements(), findBoards.getTotalPages());

        List<Board> response = findBoards.getContent();
        List<BoardDTO> list = boardService.createBoardDTOs(response, authorizationHeader);
        return new ResponseEntity<>(new PaginationBoardResponse(list, pageInfo), HttpStatus.OK);
    }

    //내 즐겨찾기 목록
    @Operation(description = "즐겨찾기 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "즐겨찾기 추가 성공"),
            @ApiResponse(responseCode = "fail", description = "즐겨찾기 추가 실패")})
    @GetMapping("/api/boards/favorites")
    public ResponseEntity<PaginationBoardResponse> findFavoriteBoards(@RequestParam int page, @RequestParam int size,
                                                                      @RequestHeader("Authorization") String authorizationHeader){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)
        Page<Board> findBoards = boardService.findFavoritesByMember(authorizationHeader, pageable);
        PageInfo pageInfo = new PageInfo(page, size, (int)findBoards.getTotalElements(), findBoards.getTotalPages());

        List<Board> response = findBoards.getContent();
        List<BoardDTO> list = boardService.createBoardDTOs(response, authorizationHeader);
        return new ResponseEntity<>(new PaginationBoardResponse(list, pageInfo), HttpStatus.OK);
    }

    //필터별 목록
    @Operation(description = "필터별 검색 테스트/ 헤더 넣지 않고 RequestParam 에 아무것도 넣지 않으면 그냥 전체 게시판, " +
            "헤더만 넣고 RequestParam 에 아무것도 넣지 않으면 전체 게시판이지만 즐겨찾기 된것은 true로 return, RequestParam 에 따라 필터별 검색 Page return")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "검색 성공"),
            @ApiResponse(responseCode = "fail", description = "검색 실패")})
    @GetMapping("/api/boards/filter")
    public ResponseEntity<PaginationBoardResponse> findBoards(@RequestParam int page, @RequestParam int size,
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
        return new ResponseEntity<>(new PaginationBoardResponse(list, pageInfo), HttpStatus.OK);
    }

    //특정 멤버가 쓴 글 목록
    @Operation(description = "특정 멤버가 쓴 글 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "글 리스트 조회 성공"),
            @ApiResponse(responseCode = "fail", description = "즐겨찾기 추가 실패")})
    @GetMapping("/api/memberBoards/{memberId}")
    public ResponseEntity<PaginationBoardResponse> memberBoards(@RequestParam int page, @RequestParam int size,
                                                                @PathVariable("memberId") Long memberId,
                                                                @RequestHeader(value = "Authorization", required = false) String authorizationHeader){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)
        Page<Board> findBoards = boardService.findBoardsByMember(memberId, pageable);
        PageInfo pageInfo = new PageInfo(page, size, (int)findBoards.getTotalElements(), findBoards.getTotalPages());

        List<Board> response = findBoards.getContent();
        List<BoardDTO> list = boardService.createBoardDTOs(response, authorizationHeader);
        return new ResponseEntity<>(new PaginationBoardResponse(list, pageInfo), HttpStatus.OK);
    }

    //게시물 즐겨찾기
    //즐겨찾기 추가 -------------------
    @Operation(description = "즐겨찾기 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "즐겨찾기 추가 성공"),
            @ApiResponse(responseCode = "fail", description = "즐겨찾기 추가 실패")})
    @PostMapping("/api/board/favorite/{boardId}")
    public ResponseEntity<String> addFavorite(@PathVariable Long boardId,
                                              @RequestHeader("Authorization") String authorizationHeader){
            boardService.addFavoriteBoard(authorizationHeader, boardId);
            return ResponseEntity.ok("추가 성공");
    }

    //즐겨찾기 삭제
    @Operation(description = "즐겨찾기 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "즐겨찾기 삭제 성공"),
            @ApiResponse(responseCode = "fail", description = "즐겨찾기 삭제 실패")})
    @DeleteMapping("/api/board/favorite/{boardId}")
    public ResponseEntity<String> removeFavorite(@PathVariable Long boardId,
                                                 @RequestHeader("Authorization") String authorizationHeader){
            boardService.removeFavoriteBoard(authorizationHeader, boardId);
            return ResponseEntity.ok("삭제 성공");
    }

    //신청 기능 -------------------
    @Operation(description = "멘토/멘티 신청 - 이미 신청한 글이거나, 자신이 쓴 글에 신청 할 경우 BAD_REQUEST" +
            "{\n" +
            "  \"content\": \"구민회 탈모\",\n" +
            "  \"date\": \"2024-02-11\",\n" +
            "  \"time\": \"09:00:00\"\n" +
            "} 이런식으로 보내면 됨")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "신청 성공"),
            @ApiResponse(responseCode = "fail", description = "신청 실패")})
    @PostMapping("/api/board/{boardId}")
    public ResponseEntity<String> boardApply(@RequestBody @Valid ApplyRequest request, @PathVariable Long boardId,
                                            @RequestHeader("Authorization") String authorizationHeader){
        applicationService.sendApply(authorizationHeader, boardId, request);
        FcmDTO fcmDTO = fcmNotificationService.createApplyFcmDTO(authorizationHeader, boardId, request);
        fcmNotificationService.sendMessageTo(fcmDTO);
        fcmNotificationService.saveFcmDetail(fcmDTO);
        return ResponseEntity.ok("신청 성공");
    }
}
