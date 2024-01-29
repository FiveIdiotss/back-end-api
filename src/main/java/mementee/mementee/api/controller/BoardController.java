package mementee.mementee.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mementee.mementee.api.controller.boardDTO.BoardDTO;
import mementee.mementee.api.controller.boardDTO.WriteBoardRequest;
import mementee.mementee.api.domain.Board;
import mementee.mementee.api.service.BoardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "글 쓰기, 글 리스트, 글 조회")
@Slf4j
public class BoardController {

    private final BoardService boardService;

    //글 쓰기--------------------------------------
    @Operation(description = "글 쓰기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "등록 성공"),
            @ApiResponse(responseCode = "fail", description = "등록 실패")})
    @PostMapping("/api/mentor_board")
    public ResponseEntity<String> saveMentorBoard(@RequestBody @Valid WriteBoardRequest request, @RequestHeader("Authorization") String authorizationHeader){
        try {
            String name = boardService.saveBoard(request, authorizationHeader);
            return ResponseEntity.ok().body(name + "님 글 등록 성공");
        } catch (Exception e) {
            // 다른 예외들을 처리
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("글 등록 실패");
        }
    }

    @Operation(description = "멘토 구인 글 전체 리스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/mentor_boards")
    public List<BoardDTO> mentorBoardList(){
        List<Board> findMentorBoards = boardService.findMentorBoards();
        List<BoardDTO> collect = findMentorBoards.stream()
                .map(m -> new BoardDTO(m.getId(), m.getTitle(), m.getContent(), m.getMember().getId(), m.getMember().getName())) //Member entity에서 꺼내와 dto에 넣음
                .collect(Collectors.toList());

        return collect;
    }

    @Operation(description = "멘티 구인 글 전체 리스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/mentee_boards")
    public List<BoardDTO> menteeBoardList(){
        List<Board> findMenteeBoards = boardService.findMenteeBoards();
        List<BoardDTO> collect = findMenteeBoards.stream()
                .map(m -> new BoardDTO(m.getId(), m.getTitle(), m.getContent(), m.getMember().getId(), m.getMember().getName())) //Member entity에서 꺼내와 dto에 넣음
                .collect(Collectors.toList());

        return collect;
    }


    @Operation(description = "멘토 구인 글 학교 별 리스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/mentor_boards/{schoolName}")
    public List<BoardDTO> mentorSchoolBoardList(@PathVariable String schoolName){
        List<Board> findSchoolMentorBoards = boardService.findSchoolMentorBoards(schoolName);
        List<BoardDTO> collect = findSchoolMentorBoards.stream()
                .map(m -> new BoardDTO(m.getId(), m.getTitle(), m.getContent(), m.getMember().getId(), m.getMember().getName())) //Member entity에서 꺼내와 dto에 넣음
                .collect(Collectors.toList());

        return collect;
    }

    @Operation(description = "멘티 구인 글 학교 별 리스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/mentee_boards/{schoolName}")
    public List<BoardDTO> menteeSchoolBoardList(@PathVariable String schoolName){
        List<Board> findSchoolMenteeBoards = boardService.findSchoolMenteeBoards(schoolName);
        List<BoardDTO> collect = findSchoolMenteeBoards.stream()
                .map(m -> new BoardDTO(m.getId(), m.getTitle(), m.getContent(), m.getMember().getId(), m.getMember().getName())) //Member entity에서 꺼내와 dto에 넣음
                .collect(Collectors.toList());

        return collect;
    }
}
