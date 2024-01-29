package mementee.mementee.api.service;

import lombok.RequiredArgsConstructor;
import mementee.mementee.api.controller.boardDTO.WriteBoardRequest;
import mementee.mementee.api.domain.Member;
import mementee.mementee.api.domain.MenteeBoard;
import mementee.mementee.api.domain.MentorBoard;
import mementee.mementee.api.repository.BoardRepository;
import mementee.mementee.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {

    @Value("${spring.jwt.secret}")
    private String secretKey;
    private final MemberService memberService;
    private final BoardRepository boardRepository;

    private String getMemberEmail(String authorizationHeader){
        String token = authorizationHeader.split(" ")[1];
        return JwtUtil.getMemberEmail(token, secretKey);
    }

    @Transactional
    public String saveMentorBoard(WriteBoardRequest request, String authorizationHeader) {
        String memberEmail = getMemberEmail(authorizationHeader);

        Member member = memberService.findMemberByEmail(memberEmail);
        MentorBoard board = new MentorBoard(request.getTitle(), request.getContent());

        member.getMentorBoards().add(board);
        boardRepository.saveMentorBoard(board);
        return member.getName();
    }

    @Transactional
    public String saveMenteeBoard(WriteBoardRequest request, String authorizationHeader) {
        String memberEmail = getMemberEmail(authorizationHeader);

        Member member = memberService.findMemberByEmail(memberEmail);
        MenteeBoard board = new MenteeBoard(request.getTitle(), request.getContent());

        member.getMenteeBoards().add(board);
        boardRepository.saveMenteeBoard(board);
        return member.getName();
    }

    //멘토 게시글 전체 조회
    public List<MentorBoard> findMentorBoards() {
        return boardRepository.findAllMentorBoards();
    }

    //멘티 게시글 전체 조회
    public List<MenteeBoard> findMenteeBoards() {
        return boardRepository.findAllMenteeBoards();
    }
}
