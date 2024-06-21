package com.team.mementee.api.repository.subBoard;

import com.team.mementee.api.domain.SubBoard;
import com.team.mementee.api.domain.SubBoardImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubBoardImageRepository extends JpaRepository<SubBoardImage, Long> {
    List<SubBoardImage> findSubBoardImageBySubBoard(SubBoard subBoard);
}
