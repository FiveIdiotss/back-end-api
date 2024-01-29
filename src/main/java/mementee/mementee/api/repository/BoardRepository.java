package mementee.mementee.api.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import mementee.mementee.api.domain.Member;
import mementee.mementee.api.domain.MenteeBoard;
import mementee.mementee.api.domain.MentorBoard;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardRepository {

    private final EntityManager em;

    //멘토 글 등록
    public void saveMentorBoard(MentorBoard board) {
        em.persist(board);
    }

    //멘티 글 등록
    public void saveMenteeBoard(MenteeBoard board) {
        em.persist(board);
    }

    //전체 멘토 게시글
    public List<MentorBoard> findAllMentorBoards() {
        return em.createQuery("select m from MentorBoard m", MentorBoard.class)
                .getResultList();
    }

    //전체 멘티 게시글
    public List<MenteeBoard> findAllMenteeBoards() {
        return em.createQuery("select m from MenteeBoard m", MenteeBoard.class)
                .getResultList();
    }
}
