package com.mementee.api.repository;

import com.mementee.api.domain.Board;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardRepository {

    private final EntityManager em;

    //글 등록
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

    //게시물 삭제
    public void deleteBoard(Board board){
       em.remove(board);
    }
}
