package mementee.mementee.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mementee.mementee.api.controller.applyDTO.ApplyRequest;
import mementee.mementee.api.controller.boardDTO.BoardDTO;
import mementee.mementee.api.controller.boardDTO.BoardInfoResponse;
import mementee.mementee.api.controller.boardDTO.WriteBoardRequest;
import mementee.mementee.api.domain.Board;
import mementee.mementee.api.domain.enumtype.BoardType;
import mementee.mementee.api.service.ApplyService;
import mementee.mementee.api.service.BoardService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "글 쓰기, 글 리스트, 글 조회")
public class BoardController {

    private final BoardService boardService;
    private final ApplyService applicationService;

    //글 쓰기--------------------------------------
    @Operation(description = "글 쓰기 - 글 작성시 상담 가능한 요일들, 상담 가능  같이 적으셈" +
            "  {\"title\": \"string\",\n" +
            "  \"content\": \"string\",\n" +
            "  \"consultTIme\": 0,\n" +
            "  \"boardType\": \"MENTEE\",\n" +
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
    @PostMapping("/api/board")
    public ResponseEntity<String> saveBoard(@RequestBody @Valid WriteBoardRequest request, @RequestHeader("Authorization") String authorizationHeader){
        try {
            String name = boardService.saveBoard(request, authorizationHeader);
            return ResponseEntity.ok().body(name + "님 글 등록 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //글 리스트로 전체 조회---------------
    //멘토,멘티 글 전체 조회
    @Operation(description =  "페이지 단위로 멘토/멘티 전체 리스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/boards")
    public Slice<BoardDTO> boardListTest(@RequestParam BoardType boardType,  @RequestParam int page, @RequestParam int size){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)

        Slice<Board> findBoards = boardService.findAllByBoardType(boardType, pageable);
        Slice<BoardDTO> slice = findBoards.map(b -> new BoardDTO(b.getId(), b.getBoardType(), b.getTitle(), b.getContent(),
                b.getMember().getYear(), b.getMember().getSchool().getName(), b.getMember().getMajor().getName(), b.getMember().getId(), b.getMember().getName()));

        return slice;
    }

    //학교별 게시물 리스트--------------------------------------
    @Operation(description = "페이지 단위로 멘토/멘티 학교별 리스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/boards/{schoolName}")
    public Slice<BoardDTO> schoolBoardList(@RequestParam BoardType boardType,  @RequestParam int page, @RequestParam int size,
                                           @PathVariable String schoolName){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)

        Slice<Board> findBoards = boardService.findAllByBoardTypeAndSchoolName(boardType, schoolName, pageable);
        Slice<BoardDTO> slice = findBoards.map(b -> new BoardDTO(b.getId(), b.getBoardType(), b.getTitle(), b.getContent(),
                b.getMember().getYear(), b.getMember().getSchool().getName(), b.getMember().getMajor().getName(), b.getMember().getId(), b.getMember().getName()));

        return slice;
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
    public ResponseEntity<?> boardInfo(@PathVariable Long boardId){
        try {
            Board board = boardService.findBoard(boardId);
            BoardDTO boardDTO = new BoardDTO(board.getId(), board.getBoardType(), board.getTitle(), board.getContent(),
                    board.getMember().getYear(), board.getMember().getSchool().getName(), board.getMember().getMajor().getName(),
                    board.getMember().getId(), board.getMember().getName());

            BoardInfoResponse response = new BoardInfoResponse(boardDTO, board.getConsultTime(), board.getTimes(),
                    board.getAvailableDays(), board.getUnavailableTimes());
            return ResponseEntity.ok(response);

        }catch (EmptyResultDataAccessException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("글 조회 실패");

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("글 조회 실패");
        }
    }

    //신청 기능 -------------------
    @Operation(description = "멘토/멘티 신청 - 이미 신청한 글이거나, 자신이 쓴 글에 신청 할 경우 BAD_REQUEST" +
            "{\n" +
            "  \"content\": \"구민회 탈머\",\n" +
            "  \"date\": \"2024-02-11\",\n" +
            "  \"time\": \"09:00:00\"\n" +
            "} 이런식으로 보내면 됨")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "신청 성공"),
            @ApiResponse(responseCode = "fail", description = "신청 실패")})
    @PostMapping("/api/board/{boardId}")
    public ResponseEntity<?> boardApply(@RequestBody @Valid ApplyRequest request, @PathVariable Long boardId,
                                        @RequestHeader("Authorization") String authorizationHeader){
        try {
            applicationService.sendApply(authorizationHeader, boardId, request);
            return ResponseEntity.ok("신청 성공");
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
