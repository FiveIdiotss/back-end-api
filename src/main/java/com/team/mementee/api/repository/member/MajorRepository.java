package com.team.mementee.api.repository.member;

import com.team.mementee.api.domain.Major;
import com.team.mementee.api.domain.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MajorRepository extends JpaRepository<Major, Long> {

    List<Major> findAllBySchool(School school);

    Major findByNameAndSchool(String name, School school);

}
