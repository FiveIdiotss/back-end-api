package com.team.mementee.api.repository.member;

import com.team.mementee.api.domain.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, Long> {

    Optional<School> findSchoolByName(String name);
}
