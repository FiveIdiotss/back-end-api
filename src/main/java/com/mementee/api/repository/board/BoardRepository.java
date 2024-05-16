package com.mementee.api.repository.board;

import com.mementee.api.domain.Board;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.School;
import com.mementee.api.domain.enumtype.BoardCategory;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface BoardRepository extends JpaRepository<Board, Long>{

    @Query("SELECT b FROM Board b WHERE b.id = :boardId")
    Board findOne(@Param("boardId")Long boardId);

    Page<Board> findBoardsByMember(Member member, Pageable pageable);       //특정 멤버가 작성한 게시물

    //-----------------
    //전체 게시판
    @NotNull
    Page<Board> findAll(@NotNull Pageable pageable);

    //내 학교
    Page<Board> findBoardsByMemberSchool(School school, Pageable pageable);

    //카테고리
    Page<Board> findBoardsByBoardCategory(BoardCategory boardCategory, Pageable pageable);

    //즐겨찾기
    @Query("SELECT f.board FROM Favorite f WHERE f.member = :member")
    Page<Board> findFavorite(@Param("member") Member member, Pageable pageable);

    //검색 (제목+내용)
    @Query("SELECT b FROM Board b WHERE ((b.content LIKE :keyWord) OR (b.title LIKE :keyWord))")
    Page<Board> findBoardsByKeyWord(@Param("keyWord") String keyWord, Pageable pageable);

    //내 학교, 검색
    @Query("SELECT b FROM Board b WHERE b.member.school = :school AND " +
            "((b.content LIKE :keyWord) OR (b.title LIKE :keyWord))")
    Page<Board> findBoardsByMemberSchoolAndKeyWord(@Param("school") School school, @Param("keyWord") String keyWord ,Pageable pageable);

    // 카테고리, 검색
    @Query("SELECT b FROM Board b WHERE b.boardCategory = :boardCategory AND " +
            "(b.content LIKE :keyWord) OR (b.title LIKE :keyWord)")
    Page<Board> findBoardsByBoardCategoryAndKeyWord(@Param("boardCategory") BoardCategory boardCategory, @Param("keyWord") String keyWord ,Pageable pageable);


    // 즐겨찾기, 검색
    @Query("SELECT b FROM Board b WHERE (b.content LIKE :keyWord) OR (b.title LIKE :keyWord) AND " +
            "b.id IN (SELECT f.board.id FROM Favorite f WHERE f.member = :member)")
    Page<Board> findBoardsByKeyWordAndFavorite(@Param("keyWord") String keyWord, @Param("member") Member member, Pageable pageable);//즐겨찾기, 검색


    //카테고리, 내 학교
    Page<Board> findBoardsByBoardCategoryAndMemberSchool(BoardCategory boardCategory,
                                                         School school, Pageable pageable);

    //내 학교, 즐겨찾기
    @Query("SELECT b FROM Board b WHERE b.member.school = :school AND " +
            "b.id IN (SELECT f.board.id FROM Favorite f WHERE f.member = :member)")
    Page<Board> findBoardsByMemberSchoolAndFavorite(@Param("school") School school,
                                                    @Param("member") Member member, Pageable pageable);

    //카테고리, 즐겨찾기
    @Query("SELECT b FROM Board b WHERE b.boardCategory = :boardCategory AND " +
            "b.id IN (SELECT f.board.id FROM Favorite f WHERE f.member = :member)")
    Page<Board> findBoardsByBoardCategoryAndFavorite(@Param("boardCategory") BoardCategory boardCategory,
                                                     @Param("member") Member member, Pageable pageable);


    //카테고리, 내 학교, 즐겨찾기
    @Query("SELECT b FROM Board b WHERE b.boardCategory = :boardCategory AND " +
            "b.member.school = :school AND " +
            "b.id IN (SELECT f.board.id FROM Favorite f WHERE f.member = :member)")
    Page<Board> findBoardsByBoardCategoryAndMemberSchoolAndFavorite(@Param("boardCategory") BoardCategory boardCategory,
                                                                    @Param("school") School school, @Param("member") Member member,
                                                                    Pageable pageable);

    //카테고리, 검색, 즐거찾기
    @Query("SELECT b FROM Board b WHERE b.boardCategory = :boardCategory AND " +
            "((b.content LIKE :keyWord) OR (b.title LIKE :keyWord)) AND" +
            " b.id IN (SELECT f.board.id FROM Favorite f WHERE f.member = :member)")
    Page<Board> findBoardsByBoardCategoryAndKeyWordAndFavorite(@Param("boardCategory") BoardCategory boardCategory,
                                                               @Param("keyWord") String keyWord, @Param("member") Member member,
                                                               Pageable pageable);
    //카테고리, 내 학교, 검색
    @Query("SELECT b FROM Board b WHERE b.boardCategory = :boardCategory AND " +
            "b.member.school = :school AND " +
            "((b.content LIKE :keyWord) OR (b.title LIKE :keyWord))")
    Page<Board> findBoardsByBoardCategoryAndMemberSchoolAndKeyWord(@Param("boardCategory") BoardCategory boardCategory,
                                                                   @Param("school") School school, @Param("keyWord") String keyWord,
                                                                   Pageable pageable);

    //내 학교, 검색, 즐겨찾기
    @Query("SELECT b FROM Board b WHERE b.member.school = :school AND " +
            "((b.content LIKE :keyWord) OR (b.title LIKE :keyWord)) AND " +
            "b.id IN (SELECT f.board.id FROM Favorite f WHERE f.member = :member)")
    Page<Board> findBoardsByMemberSchoolAndKeyWordAndFavorite(@Param("school") School school, @Param("keyWord") String keyWord,
                                                              @Param("member") Member member,
                                                              Pageable pageable);


    //내 학교, 검색, 카테고리, 즐거챶기
    @Query("SELECT b FROM Board b WHERE b.member.school = :school AND " +
            "((b.content LIKE :keyWord) OR (b.title LIKE :keyWord)) AND " +
            "b.boardCategory = :boardCategory AND " +
            "b.id IN (SELECT f.board.id FROM Favorite f WHERE f.member = :member)")
    Page<Board> findBoardsByMemberSchoolAndKeyWordAndBoardCategoryAndFavorite(@Param("school") School school, @Param("keyWord") String keyWord,
                                                                              @Param("boardCategory") BoardCategory boardCategory, @Param("member") Member member,
                                                                              Pageable pageable);

    //----


}
