package mementee.mementee.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mementee.mementee.api.controller.applicationDTO.ApplicationRequest;
import mementee.mementee.api.controller.boardDTO.BoardDTO;
import mementee.mementee.api.controller.boardDTO.BoardInfoResponse;
import mementee.mementee.api.controller.boardDTO.WriteBoardRequest;
import mementee.mementee.api.domain.Board;
import mementee.mementee.api.domain.enumtype.BoardType;
import mementee.mementee.api.service.ApplicationService;
import mementee.mementee.api.service.BoardService;
import mementee.mementee.api.service.MemberService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "글 쓰기, 글 리스트, 글 조회")
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final MemberService memberService;
    private final ApplicationService applicationService;

    //글 쓰기--------------------------------------
    @Operation(description = "글 쓰기 - 글 작성시 상담 가능한 요일들, 상담 가능 시작시간 ~ 종료시간 같이 적으셈" +
            "{\n" +
            "  \"title\": \"구민회 씹ㅌ 라모\",\n" +
            "  \"content\": \"구민회 씹탈뮤ㅗ\",\n" +
            "  \"boardType\": \"MENTEE\",\n" +
            "  \"startTime\": \"00:00:00\",\n" +
            "  \"lastTime\": \"02:00:00\",\n" +
            "  \"availableDays\": [\n" +
            "    \"MONDAY\"\n" +
            " ]\n" +
            "} 시간 입력시 이런식으로 작성 바람")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "등록 성공"),
            @ApiResponse(responseCode = "fail", description = "등록 실패")})
    @PostMapping("/api/board")
    public ResponseEntity<String> saveMentorBoard(@RequestBody @Valid WriteBoardRequest request, @RequestHeader("Authorization") String authorizationHeader){
        try {
            String name = boardService.saveBoard(request, authorizationHeader);
            return ResponseEntity.ok().body(name + "님 글 등록 성공");
        } catch (Exception e) {
            // 다른 예외들을 처리
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("글 등록 실패");
        }
    }

    //글 리스트로 전체 조회---------------
    //무한 스크롤 용 멘토,멘티 글 전체 조회
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

    @Operation(description = "페이지 단위로 멘토/멘티 학교별 리스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/boards/{schoolName}")
    public Slice<BoardDTO> schoolBoardList(@RequestParam BoardType boardType,  @RequestParam int page, @RequestParam int size, @PathVariable String schoolName){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)

        Slice<Board> findBoards = boardService.findAllByBoardTypeAndSchoolName(boardType, schoolName, pageable);
        Slice<BoardDTO> slice = findBoards.map(b -> new BoardDTO(b.getId(), b.getBoardType(), b.getTitle(), b.getContent(),
                b.getMember().getYear(), b.getMember().getSchool().getName(), b.getMember().getMajor().getName(), b.getMember().getId(), b.getMember().getName()));

        return slice;
    }

    //게시글 조회 --------------------
    @Operation(description = "글 조회")
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

            BoardInfoResponse response = new BoardInfoResponse(boardDTO, board.getStartTime(), board.getLastTime(), board.getAvailableDays());
            return ResponseEntity.ok(response);

        }catch (EmptyResultDataAccessException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("글 조회 실패");

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("글 조회 실패");
        }
    }

    //신청 기능 -------------------
    @Operation(description = "멘토/멘티 신청 - 이미 신청한 글이거나, 자신이 쓴 글에 신청 할 경우 BAD_REQUEST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "신청 성공"),
            @ApiResponse(responseCode = "fail", description = "신청 실패")})
    @PostMapping("/api/board/{boardId}")
    public ResponseEntity<?> boardApply(@RequestBody @Valid ApplicationRequest request, @PathVariable Long boardId, @RequestHeader("Authorization") String authorizationHeader){
        try {
            applicationService.sendApply(authorizationHeader, boardId, request);

            return ResponseEntity.ok("신청 성공");

        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
