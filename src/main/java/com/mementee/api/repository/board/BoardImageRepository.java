package com.mementee.api.repository.board;

import com.mementee.api.domain.Board;
import com.mementee.api.domain.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardImageRepository  extends JpaRepository<BoardImage, Long>{

    List<BoardImage> findBoardImagesByBoard(Board board);

}
