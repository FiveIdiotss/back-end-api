package com.team.mementee.api.repository.subBoard;

import com.team.mementee.api.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubBoardLikeRepository extends JpaRepository<SubBoardLike, Long> {

    Optional<SubBoardLike> findSubBoardLikeByMemberAndSubBoard(Member member, SubBoard subBoard);
}
