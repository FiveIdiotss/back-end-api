package com.team.mementee.api.repository;

import com.team.mementee.api.domain.Matching;
import com.team.mementee.api.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    List<Matching> findMatchingsByMentor(Member mentor);

    List<Matching> findMatchingsByMentee(Member mentee);


}
