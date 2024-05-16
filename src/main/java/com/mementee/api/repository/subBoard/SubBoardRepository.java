package com.mementee.api.repository.subBoard;

import com.mementee.api.domain.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubBoardRepository extends JpaRepository<SubBoard, Long> {

    @NotNull
    Page<SubBoard> findAll(@NotNull Pageable pageable);

    @Query("SELECT b FROM SubBoard b WHERE b.id = :subBoardId")
    SubBoard findOne(@Param("subBoardId")Long subBoardId);

    //특정 멤버가 작성한 게시물
    Page<SubBoard> findSubBoardsByMember(Member member, Pageable pageable);

    //내 학교
    Page<SubBoard> findSubBoardsByMemberSchool(School school, Pageable pageable);

    //검색 (제목+내용)
    @Query("SELECT s FROM SubBoard s WHERE ((s.content LIKE :keyWord) OR (s.title LIKE :keyWord))")
    Page<SubBoard> findSubBoardsByKeyWord(@Param("keyWord") String keyWord, Pageable pageable);

}
