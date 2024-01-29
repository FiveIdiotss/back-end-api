package mementee.mementee.api.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import mementee.mementee.api.domain.Board;
import org.springframework.stereotype.Repository;

import java.util.List;

import static mementee.mementee.api.domain.enumtype.BoardType.MENTOR;

@Repository
@RequiredArgsConstructor
public class BoardRepository {

    private final EntityManager em;

    //멘토등록
    public void saveBoard(Board board) {
        em.persist(board);
    }

    //멘토 게시글 조회
    public Board findBoard(Long boardId) {
        return em.find(Board.class, boardId);
    }


    //전체 멘토 게시글
    public List<Board> findAllMentorBoards() {
        return em.createQuery("select b from Board b where b.boardType = 'MENTOR'", Board.class)
                .getResultList();
    }

    //전체 멘티 게시글
    public List<Board> findAllMenteeBoards() {
        return em.createQuery("select b from Board b where b.boardType = 'MENTEE'", Board.class)
                .getResultList();
    }

    //전체 멘토 게시글
    public List<Board> findSchoolMentorBoards(String schoolName) {
        return em.createQuery("select b from Board b where b.boardType = 'MENTOR' AND b.member.school.name =: schoolName ", Board.class)
                .setParameter("schoolName", schoolName)
                .getResultList();
    }

    //전체 멘티 게시글
    public List<Board> findSchoolMenteeBoards(String schoolName) {
        return em.createQuery("select b from Board b where b.boardType = 'MENTEE' AND b.member.school.name =: schoolName ", Board.class)
                .setParameter("schoolName", schoolName)
                .getResultList();
    }
}
