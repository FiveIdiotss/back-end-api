package com.mementee.api.repository.subBoard;

import com.mementee.api.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubBoardLikeRepository extends JpaRepository<SubBoardLike, Long> {
    @Query("SELECT s FROM SubBoardLike s WHERE s.id = :subBoardLikeId")
    SubBoardLike findOne(@Param("subBoardLikeId") Long subBoardLikeId);

    Optional<SubBoardLike> findSubBoardLikeByMemberAndSubBoard(Member member, SubBoard subBoard);
}
