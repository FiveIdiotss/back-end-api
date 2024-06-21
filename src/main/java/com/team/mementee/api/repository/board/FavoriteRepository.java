package com.team.mementee.api.repository.board;

import com.team.mementee.api.domain.Board;
import com.team.mementee.api.domain.Favorite;
import com.team.mementee.api.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findFavoriteByMemberAndBoard(Member member, Board board);

}
