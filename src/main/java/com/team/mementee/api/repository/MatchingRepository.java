package com.team.mementee.api.repository;

import com.team.mementee.api.domain.Matching;
import com.team.mementee.api.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    @Query("SELECT ma FROM Matching ma " +
            "JOIN FETCH ma.mentor m " +
            "WHERE m = :mentor")
    List<Matching> findMatchingsByMentor(@Param("mentor") Member mentor);

    @Query("SELECT ma FROM Matching ma " +
            "JOIN FETCH ma.mentee m " +
            "WHERE m = :mentee")
    List<Matching> findMatchingsByMentee(@Param("mentee") Member mentee);


}
