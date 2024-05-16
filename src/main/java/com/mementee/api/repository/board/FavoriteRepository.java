package com.mementee.api.repository.board;

import com.mementee.api.domain.Board;
import com.mementee.api.domain.Favorite;
import com.mementee.api.domain.Member;
import jakarta.persistence.NoResultException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    @Query("SELECT f FROM Favorite f WHERE f.id = :favoriteId")
    Favorite findOne(@Param("favoriteId") Long favoriteId);

    Optional<Favorite> findFavoriteByMemberAndBoard(Member member, Board board);

}
