package mementee.mementee.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import mementee.mementee.domain.Board;
import mementee.mementee.domain.Member;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardRepository {

    private final EntityManager em;

    //글 등록
    public void save(Board board) {
        em.persist(board);
    }
}
