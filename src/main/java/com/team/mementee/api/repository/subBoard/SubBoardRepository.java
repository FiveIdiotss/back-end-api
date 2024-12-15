package com.team.mementee.api.repository.subBoard;

import com.team.mementee.api.domain.Member;
import com.team.mementee.api.domain.School;
import com.team.mementee.api.domain.SubBoard;
import com.team.mementee.api.domain.enumtype.BoardCategory;
import com.team.mementee.api.domain.enumtype.SubBoardType;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubBoardRepository extends JpaRepository<SubBoard, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SubBoard s WHERE s.id = :subBoardId")
    Optional<SubBoard> findByIdWithLock(@Param("subBoardId") Long subBoardId);

    // 주간 인기글 상위 5개 추출
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND s.likeCount > 0 AND s.createdAt >= :startDate AND s.createdAt < :endDate ORDER BY s.likeCount DESC")
    List<SubBoard> findTop5ByLikeCountInLastWeek(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate,
                                                 @Param("subBoardType") SubBoardType subBoardType,
                                                 Pageable pageable);

    // 제목 검색
    List<SubBoard> findAllByTitleContaining(@Param("query") String query);

    List<SubBoard> findAllByContentContaining(@Param("query") String query);

    // 특정 멤버가 작성한 게시물
    Page<SubBoard> findSubBoardsBySubBoardTypeAndMember(@Param("subBoardType") SubBoardType subBoardType,
                                                        @Param("member") Member member,
                                                        Pageable pageable);

    // 전체 글
    Page<SubBoard> findAllBySubBoardType(@Param("subBoardType") SubBoardType subBoardType,
                                         Pageable pageable);

    // 카테고리별
    Page<SubBoard> findSubBoardsBySubBoardTypeAndBoardCategory(@Param("subBoardType") SubBoardType subBoardType,
                                                               @Param("boardCategory") BoardCategory boardCategory,
                                                               Pageable pageable);

    // 내 학교
    Page<SubBoard> findSubBoardsBySubBoardTypeAndMemberSchool(@Param("subBoardType") SubBoardType subBoardType,
                                                              @Param("school") School school,
                                                              Pageable pageable);

    // 좋아요
    @Query("SELECT sl.subBoard FROM SubBoardLike sl WHERE sl.subBoard.subBoardType = :subBoardType AND sl.member = :member")
    Page<SubBoard> findSubBoardLikesBySubBoardTypeAndMember(@Param("subBoardType") SubBoardType subBoardType,
                                                            @Param("member") Member member,
                                                            Pageable pageable);

    // 검색 (제목+내용)
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND (s.content LIKE :keyWord OR s.title LIKE :keyWord)")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndKeyWord(@Param("subBoardType") SubBoardType subBoardType,
                                                         @Param("keyWord") String keyWord,
                                                         Pageable pageable);

    // 내 학교, 검색
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND (s.content LIKE :keyWord OR s.title LIKE :keyWord) AND s.member.school = :school")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndMemberSchoolAndKeyWord(@Param("subBoardType") SubBoardType subBoardType,
                                                                        @Param("school") School school,
                                                                        @Param("keyWord") String keyWord,
                                                                        Pageable pageable);

    // 카테고리, 검색
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND (s.content LIKE :keyWord OR s.title LIKE :keyWord) AND s.boardCategory = :boardCategory")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndBoardCategoryAndKeyWord(@Param("subBoardType") SubBoardType subBoardType,
                                                                         @Param("boardCategory") BoardCategory boardCategory,
                                                                         @Param("keyWord") String keyWord,
                                                                         Pageable pageable);

    // 좋아요, 검색
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND (s.content LIKE :keyWord OR s.title LIKE :keyWord) AND s.id IN (SELECT sl.subBoard.id FROM SubBoardLike sl WHERE sl.member = :member)")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndKeyWordAndSubBoardLike(@Param("subBoardType") SubBoardType subBoardType,
                                                                        @Param("keyWord") String keyWord,
                                                                        @Param("member") Member member,
                                                                        Pageable pageable);

    // 카테고리, 내 학교
    Page<SubBoard> findSubBoardsBySubBoardTypeAndBoardCategoryAndMemberSchool(@Param("subBoardType") SubBoardType subBoardType,
                                                                              @Param("boardCategory") BoardCategory boardCategory,
                                                                              @Param("school") School school,
                                                                              Pageable pageable);

    // 내 학교, 좋아요
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND s.member.school = :school AND s.id IN (SELECT sl.subBoard.id FROM SubBoardLike sl WHERE sl.member = :member)")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndMemberSchoolAndSubBoardLike(@Param("subBoardType") SubBoardType subBoardType,
                                                                             @Param("school") School school,
                                                                             @Param("member") Member member,
                                                                             Pageable pageable);

    // 카테고리, 좋아요
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND s.boardCategory = :boardCategory AND s.id IN (SELECT sl.subBoard.id FROM SubBoardLike sl WHERE sl.member = :member)")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndBoardCategoryAndSubBoardLike(@Param("subBoardType") SubBoardType subBoardType,
                                                                              @Param("boardCategory") BoardCategory boardCategory,
                                                                              @Param("member") Member member,
                                                                              Pageable pageable);

    // 카테고리, 내 학교, 좋아요
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND s.boardCategory = :boardCategory AND s.member.school = :school AND s.id IN (SELECT sl.subBoard.id FROM SubBoardLike sl WHERE sl.member = :member)")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndBoardCategoryAndMemberSchoolAndSubBoardLike(@Param("subBoardType") SubBoardType subBoardType,
                                                                                             @Param("boardCategory") BoardCategory boardCategory,
                                                                                             @Param("school") School school,
                                                                                             @Param("member") Member member,
                                                                                             Pageable pageable);

    // 카테고리, 검색, 좋아요
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND (s.content LIKE :keyWord OR s.title LIKE :keyWord) AND s.boardCategory = :boardCategory AND s.id IN (SELECT sl.subBoard.id FROM SubBoardLike sl WHERE sl.member = :member)")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndBoardCategoryAndKeyWordAndSubBoardLike(@Param("subBoardType") SubBoardType subBoardType,
                                                                                        @Param("boardCategory") BoardCategory boardCategory,
                                                                                        @Param("keyWord") String keyWord,
                                                                                        @Param("member") Member member,
                                                                                        Pageable pageable);

    // 카테고리, 내 학교, 검색
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND (s.content LIKE :keyWord OR s.title LIKE :keyWord) AND s.member.school = :school AND s.boardCategory = :boardCategory")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndBoardCategoryAndMemberSchoolAndKeyWord(@Param("subBoardType") SubBoardType subBoardType,
                                                                                        @Param("boardCategory") BoardCategory boardCategory,
                                                                                        @Param("school") School school,
                                                                                        @Param("keyWord") String keyWord,
                                                                                        Pageable pageable);

    // 내 학교, 검색, 좋아요
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND (s.content LIKE :keyWord OR s.title LIKE :keyWord) AND s.member.school = :school AND s.id IN (SELECT sl.subBoard.id FROM SubBoardLike sl WHERE sl.member = :member)")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndMemberSchoolAndKeyWordAndSubBoardLike(@Param("subBoardType") SubBoardType subBoardType,
                                                                                       @Param("school") School school,
                                                                                       @Param("keyWord") String keyWord,
                                                                                       @Param("member") Member member,
                                                                                       Pageable pageable);

    // 내 학교, 검색, 카테고리, 좋아요
    @Query("SELECT s FROM SubBoard s WHERE s.subBoardType = :subBoardType AND (s.content LIKE :keyWord OR s.title LIKE :keyWord) AND s.member.school = :school AND s.boardCategory = :boardCategory AND s.id IN (SELECT sl.subBoard.id FROM SubBoardLike sl WHERE sl.member = :member)")
    Page<SubBoard> findSubBoardsBySubBoardTypeAndMemberSchoolAndKeyWordAndBoardCategoryAndSubBoardLike(@Param("subBoardType") SubBoardType subBoardType,
                                                                                                       @Param("school") School school,
                                                                                                       @Param("keyWord") String keyWord,
                                                                                                       @Param("boardCategory") BoardCategory boardCategory,
                                                                                                       @Param("member") Member member,
                                                                                                       Pageable pageable);
}