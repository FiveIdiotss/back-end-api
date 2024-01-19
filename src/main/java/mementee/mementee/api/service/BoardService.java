package mementee.mementee.api.service;

import lombok.RequiredArgsConstructor;
import mementee.mementee.api.domain.Board;
import mementee.mementee.api.repository.BoardRepository;
import mementee.mementee.api.repository.MemberRepository;
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
