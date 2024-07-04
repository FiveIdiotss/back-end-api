package com.team.mementee.api.repository.subBoard;

import com.mementee.api.domain.*;
import com.team.mementee.api.domain.Member;
import com.team.mementee.api.domain.SubBoard;
import com.team.mementee.api.domain.SubBoardLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubBoardLikeRepository extends JpaRepository<SubBoardLike, Long> {

    Optional<SubBoardLike> findSubBoardLikeByMemberAndSubBoard(Member member, SubBoard subBoard);
}
