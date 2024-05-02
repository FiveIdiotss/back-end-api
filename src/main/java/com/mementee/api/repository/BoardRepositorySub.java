package com.mementee.api.repository;

import com.mementee.api.domain.Board;
import com.mementee.api.domain.enumtype.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface BoardRepositorySub extends JpaRepository<Board, Long> {
    Slice<Board> findAllByBoardType(BoardType boardType, Pageable pageable);
    @Query("SELECT b FROM Board b WHERE b.boardType = :boardType AND b.member.school.name = :schoolName")
    Slice<Board> findAllByBoardTypeAndSchoolName(@Param("boardType") BoardType boardType, @Param("schoolName") String schoolName, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.boardType = :boardType")
    Page<Board> findAllByBoardTypeByPage(@Param("boardType") BoardType boardType, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.boardType = :boardType AND b.member.school.name = :schoolName")
    Page<Board> findAllByBoardTypeAndSchoolNameByPage(@Param("boardType") BoardType boardType, @Param("schoolName") String schoolName, Pageable pageable);
}
