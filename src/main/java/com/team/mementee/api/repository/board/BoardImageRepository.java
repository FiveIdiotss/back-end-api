package com.team.mementee.api.repository.board;

import com.team.mementee.api.domain.Board;
import com.team.mementee.api.domain.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardImageRepository  extends JpaRepository<BoardImage, Long>{

    List<BoardImage> findBoardImagesByBoard(Board board);

    @Query("SELECT bi FROM BoardImage bi WHERE bi.board = :board ORDER BY bi.id ASC")
    List<BoardImage> findFirstByBoardOrderByIdAsc(@Param("board") Board board);
}
