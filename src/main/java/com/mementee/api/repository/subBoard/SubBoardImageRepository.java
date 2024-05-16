package com.mementee.api.repository.subBoard;

import com.mementee.api.domain.SubBoard;
import com.mementee.api.domain.SubBoardImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubBoardImageRepository extends JpaRepository<SubBoardImage, Long> {
    List<SubBoardImage> findSubBoardImageBySubBoard(SubBoard subBoard);
}
