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
import mementee.mementee.api.controller.memberDTO.MemberDTO;
import mementee.mementee.api.domain.MenteeBoard;
import mementee.mementee.api.domain.MentorBoard;
import mementee.mementee.api.domain.Member;
import mementee.mementee.security.JwtUtil;
import mementee.mementee.api.service.BoardService;
import mementee.mementee.api.service.MemberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "글 쓰기 테스트 옹")
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final MemberService memberService;

    //글 쓰기--------------------------------------
    @Operation(description = "멘토가 쓰는 글")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "등록 성공"),
            @ApiResponse(responseCode = "fail", description = "등록 실패")})
    @PostMapping("/api/mentor_board")
    public ResponseEntity<String> saveMentorBoard(@RequestBody @Valid WriteBoardRequest request, @RequestHeader("Authorization") String authorizationHeader){
        try {
            String name = boardService.saveMentorBoard(request, authorizationHeader);
            return ResponseEntity.ok().body(name + "님 글 등록 성공");
        } catch (Exception e) {
            // 다른 예외들을 처리
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("글 등록 실패");
        }
    }

    @Operation(description = "멘티가 쓰는 글")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "등록 성공"),
            @ApiResponse(responseCode = "fail", description = "등록 실패")})
    @PostMapping("/api/mentee_board")
    public ResponseEntity<String> saveMenteeBoard(@RequestBody @Valid WriteBoardRequest request, @RequestHeader("Authorization") String authorizationHeader){
        try {
            String name = boardService.saveMenteeBoard(request, authorizationHeader);
            return ResponseEntity.ok().body(name + "님 글 등록 성공");
        } catch (Exception e) {
            // 다른 예외들을 처리
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("글 등록 실패");
        }
    }

    @Operation(description = "멘토 글 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/mentor_boards")
    public List<BoardDTO> mentorBoardList(){
        List<MentorBoard> findMentorBoards = boardService.findMentorBoards();
        List<BoardDTO> collect = findMentorBoards.stream()
                .map(m -> new BoardDTO(m.getId(), m.getTitle(), m.getContent(), m.getMember().getId(), m.getMember().getName())) //Member entity에서 꺼내와 dto에 넣음
                .collect(Collectors.toList());

        return collect;
    }

    @Operation(description = "멘티 글 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/mentee_boards")
    public List<BoardDTO> menteeBoardList(){
        List<MenteeBoard> findMenteeBoards = boardService.findMenteeBoards();
        List<BoardDTO> collect = findMenteeBoards.stream()
                .map(m -> new BoardDTO(m.getId(), m.getTitle(), m.getContent(), m.getMember().getId(), m.getMember().getName())) //Member entity에서 꺼내와 dto에 넣음
                .collect(Collectors.toList());

        return collect;
    }
}
