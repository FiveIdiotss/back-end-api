package mementee.mementee.api.repository;

import mementee.mementee.api.domain.Board;
import mementee.mementee.api.domain.enumtype.BoardType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


public interface BoardRepositorySub extends JpaRepository<Board, Long> {
    Slice<Board> findAllByBoardType(BoardType boardType, Pageable pageable);
    @Query("SELECT b FROM Board b WHERE b.boardType = :boardType AND b.member.school.name = :schoolName")
    Slice<Board> findAllByBoardTypeAndSchoolName(@Param("boardType") BoardType boardType, @Param("schoolName") String schoolName, Pageable pageable);

}
