package mementee.mementee.api.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mementee.mementee.api.controller.boardDTO.WriteBoardRequest;
import mementee.mementee.api.domain.Board;
import mementee.mementee.api.domain.Member;
import mementee.mementee.security.JwtUtil;
import mementee.mementee.api.service.BoardService;
import mementee.mementee.api.service.MemberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "글 쓰기 테스트 옹")
@Slf4j
public class BoardController {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    private final BoardService boardService;
    private final MemberService memberService;

    //글 쓰기--------------------------------------
    @Operation(description = "글쓰기 테스트 용 (로그인 토큰 테스트 용)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "등록 성공"),
            @ApiResponse(responseCode = "fail", description = "등록 실패")})
    @PostMapping("/api/board")
    public ResponseEntity<String> saveBoard(@RequestBody @Valid WriteBoardRequest request, @RequestHeader("Authorization") String authorizationHeader){
        try {
            String token = authorizationHeader.split(" ")[1];
            String memberEmail = JwtUtil.getMemberEmail(token, secretKey);

            //System.out.println("memberEmail = " + memberEmail);

            Member member = memberService.findMemberByEmail(memberEmail);
            Board board = new Board(request.getTitle(), request.getContent());

            boardService.save(board);

            return ResponseEntity.ok().body(member.getName() + "님 글 등록 성공");
        } catch (Exception e) {
            // 다른 예외들을 처리하거나 로깅 등을 수행할 수 있습니다.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("글 등록 실패");
        }
    }
}
