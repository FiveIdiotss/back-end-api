package com.mementee.api.repository;

import com.mementee.api.domain.SubBoard;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubBoardRepositorySub extends JpaRepository<SubBoard, Long> {

    @NotNull
    Page<SubBoard> findAll(@NotNull Pageable pageable);

    @Query("SELECT b FROM SubBoard b WHERE b.member.school.name = :schoolName")
    Page<SubBoard> findAllSubBoardBySchoolNameByPage(@Param("schoolName") String schoolName, Pageable pageable);
}
