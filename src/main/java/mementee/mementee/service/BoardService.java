package mementee.mementee.service;

import lombok.RequiredArgsConstructor;
import mementee.mementee.domain.Board;
import mementee.mementee.domain.Major;
import mementee.mementee.domain.Member;
import mementee.mementee.domain.School;
import mementee.mementee.repository.BoardRepository;
import mementee.mementee.repository.MemberRepository;
import mementee.mementee.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Transactional
    public void save(Board board) {
            boardRepository.save(board);
    }
}
