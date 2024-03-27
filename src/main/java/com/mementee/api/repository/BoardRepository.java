package com.mementee.api.repository;

import com.mementee.api.domain.Board;
import com.mementee.api.domain.BoardImage;
import com.mementee.api.domain.Favorite;
import com.mementee.api.domain.RefreshToken;
import com.mementee.api.domain.enumtype.BoardType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    //------------------------------------

    public Favorite findFavorite(Long favoriteId){
        return em.find(Favorite.class, favoriteId);
    }

    //게시물 즐겨찾기 삭제
    public void deleteBoard(Favorite favorite){
       em.remove(favorite);
    }

    //게시물 즐겨찾기
    public void saveFavorite(Favorite favorite){
        em.persist(favorite);
    }

    public void deleteFavorite(Favorite favorite){
        em.remove(favorite);
    }

    public List<Board> findFavoriteBoards(Long memberId, BoardType boardType){
        return em.createQuery("select f.board from Favorite f where f.member.id =: memberId and f.board.boardType = :boardType", Board.class)
                .setParameter("memberId", memberId)
                .setParameter("boardType", boardType)
                .getResultList();
    }

    //즐겨찾기 중복 방지
    public Optional<Favorite> findFavoriteByMemberIdAndBoardId(Long memberId, Long boardId){
        try {
            Favorite favorite = em.createQuery("select f from Favorite f where f.member.id = :memberId and f.board.id = : boardId" , Favorite.class)
                    .setParameter("memberId", memberId)
                    .setParameter("boardId", boardId)
                    .getSingleResult();
            return Optional.ofNullable(favorite);
        }catch (NoResultException e){
            return Optional.empty();
        }
    }

    //멤버가 쓴 글 목록
    public List<Board> findMemberBoards(Long memberId, BoardType boardType){
        return em.createQuery("select b from Board b where b.member.id =: memberId and b.boardType = :boardType", Board.class)
                .setParameter("memberId", memberId)
                .setParameter("boardType", boardType)
                .getResultList();
    }


    //게시판 사진 등롱
    public void saveBoardImage(BoardImage boardImage){
        em.persist(boardImage);
    }
}
