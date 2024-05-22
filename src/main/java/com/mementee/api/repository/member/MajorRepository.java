package com.mementee.api.repository.member;

import com.mementee.api.domain.Major;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MajorRepository extends JpaRepository<Major, Long> {

    List<Major> findAllByName(String name);
}
