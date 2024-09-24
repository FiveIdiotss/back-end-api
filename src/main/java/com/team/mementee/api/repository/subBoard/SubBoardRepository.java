package com.team.mementee.api.repository.subBoard;

import com.team.mementee.api.domain.Member;
import com.team.mementee.api.domain.School;
import com.team.mementee.api.domain.SubBoard;
import com.team.mementee.api.domain.enumtype.BoardCategory;
import com.team.mementee.api.domain.enumtype.SubBoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubBoardRepository extends JpaRepository<SubBoard, Long> {

    // 제목 검색
    List<SubBoard> findAllByTitleContaining(String query);
    List<SubBoard> findAllByContentContaining(String query);

    //특정 멤버가 작성한 게시물
    Page<SubBoard> findSubBoardsBySubBoardTypeAndMember(SubBoardType subBoardType, Member member, Pageable pageable);

    //전체 글--------
    Page<SubBoard> findAllBySubBoardType(SubBoardType subBoardType, Pageable pageable);

    //카테고리별
    Page<SubBoard> findSubBoardsBySubBoardTypeAndBoardCategory(SubBoardType subBoardType, BoardCategory boardCategory, Pageable pageable);

    //내 학교
    Page<SubBoard> findSubBoardsBySubBoardTypeAndMemberSchool(SubBoardType subBoardType, School school, Pageable pageable);

    //좋아요
    @Query("SELECT sl.subBoard FROM SubBoardLike sl WHERE sl.subBoard.subBoardType = :subBoardType AND " +
            "sl.member = :member")
    Page<SubBoard> findSubBoardLikesBySubBoardTypeAndMember(SubBoardType subBoardType, @Param("member") Member member, Pageable pageable);

    //검색 (제목+내용)
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND " +
            "(s.content LIKE :keyWord OR s.title LIKE :keyWord)")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndKeyWord(SubBoardType subBoardType, @Param("keyWord") String keyWord, Pageable pageable);


    //내 학교, 검색
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND " +
            "(s.content LIKE :keyWord OR s.title LIKE :keyWord) AND " +
            "s.member.school = :school")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndMemberSchoolAndKeyWord(SubBoardType subBoardType, @Param("school") School school,
                                                                        @Param("keyWord") String keyWord ,Pageable pageable);

    //카테고리, 검색
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND " +
            "(s.content LIKE :keyWord OR s.title LIKE :keyWord) " +
            "AND s.boardCategory = :boardCategory")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndBoardCategoryAndKeyWord(SubBoardType subBoardType, @Param("boardCategory") BoardCategory boardCategory, @Param("keyWord") String keyWord ,
                                                                         Pageable pageable);


    //좋아요, 검색
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND " +
            "(s.content LIKE :keyWord OR s.title LIKE :keyWord) AND " +
            "s.id IN (SELECT sl.subBoard.id FROM SubBoardLike sl WHERE sl.member = :member)")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndKeyWordAndSubBoardLike(SubBoardType subBoardType, @Param("keyWord") String keyWord,
                                                                        @Param("member") Member member, Pageable pageable);


    //카테고리, 내 학교
    Page<SubBoard> findSubBoardsBySubBoardTypeAndBoardCategoryAndMemberSchool(SubBoardType subBoardType, BoardCategory boardCategory,
                                                                              School school, Pageable pageable);

    //내 학교, 좋아요
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND " +
            "s.member.school = :school AND " +
            "s.id IN (SELECT sl.subBoard.id FROM SubBoardLike sl WHERE sl.member = :member)")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndMemberSchoolAndSubBoardLike(SubBoardType subBoardType, @Param("school") School school,
                                                                             @Param("member") Member member, Pageable pageable);

    //카테고리, 좋아요
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND " +
            "s.boardCategory = :boardCategory AND " +
            "s.id IN (SELECT sl.subBoard.id FROM SubBoardLike sl WHERE sl.member = :member)")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndBoardCategoryAndSubBoardLike(SubBoardType subBoardType, @Param("boardCategory") BoardCategory boardCategory,
                                                                              @Param("member") Member member, Pageable pageable);


    //카테고리, 내 학교, 좋아요
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND " +
            "s.boardCategory = :boardCategory AND " +
            "s.member.school = :school AND " +
            "s.id IN (SELECT sl.subBoard.id FROM SubBoardLike sl WHERE sl.member = :member)")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndBoardCategoryAndMemberSchoolAndSubBoardLike(SubBoardType subBoardType, @Param("boardCategory") BoardCategory boardCategory,
                                                                                             @Param("school") School school, @Param("member") Member member, Pageable pageable);

    //카테고리, 검색, 좋아요
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND " +
            "(s.content LIKE :keyWord OR s.title LIKE :keyWord) AND " +
            "s.boardCategory = :boardCategory AND " +
            "s.id IN (SELECT sl.subBoard.id FROM SubBoardLike sl WHERE sl.member = :member)")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndBoardCategoryAndKeyWordAndSubBoardLike(SubBoardType subBoardType, @Param("boardCategory") BoardCategory boardCategory,
                                                                                    @Param("keyWord") String keyWord, @Param("member") Member member, Pageable pageable);
    //카테고리, 내 학교, 검색
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND " +
            "(s.content LIKE :keyWord OR s.title LIKE :keyWord) AND " +
            "s.member.school = :school AND " +
            "s.boardCategory = :boardCategory")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndBoardCategoryAndMemberSchoolAndKeyWord(SubBoardType subBoardType, @Param("boardCategory") BoardCategory boardCategory,
                                                                                        @Param("school") School school, @Param("keyWord") String keyWord, Pageable pageable);
    //내 학교, 검색, 좋아요
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND " +
            " (s.content LIKE :keyWord OR s.title LIKE :keyWord) AND " +
            "s.member.school = :school AND " +
            "s.id IN (SELECT sl.subBoard.id FROM SubBoardLike sl WHERE sl.member = :member)")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndMemberSchoolAndKeyWordAndSubBoardLike(SubBoardType subBoardType, @Param("school") School school,
                                                                                       @Param("keyWord") String keyWord, @Param("member") Member member, Pageable pageable);


    //내 학교, 검색, 카테고리, 좋아요
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND " +
            "(s.content LIKE :keyWord OR s.title LIKE :keyWord) AND " +
            "s.member.school = :school AND " +
            "s.boardCategory = :boardCategory AND " +
            "s.id IN (SELECT sl.subBoard.id FROM SubBoardLike sl WHERE sl.member = :member)")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndMemberSchoolAndKeyWordAndBoardCategoryAndSubBoardLike(SubBoardType subBoardType, @Param("school") School school, @Param("keyWord") String keyWord,
                                                                                                       @Param("boardCategory") BoardCategory boardCategory, @Param("member") Member member, Pageable pageable);
    //----
}
