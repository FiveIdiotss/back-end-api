package com.team.mementee.api.repository.subBoard;

import com.team.mementee.api.domain.SubBoard;
import com.team.mementee.api.domain.SubBoardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubBoardImageRepository extends JpaRepository<SubBoardImage, Long> {
    List<SubBoardImage> findSubBoardImageBySubBoard(SubBoard subBoard);

    @Query("SELECT si FROM SubBoardImage si WHERE si.subBoard = :subBoard ORDER BY si.id ASC")
    Optional<SubBoardImage> findFirstBySubBoardOrderByIdAsc(@Param("subBoard") SubBoard subBoard);
}
