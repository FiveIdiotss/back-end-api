package com.mementee.api.repository;

import com.mementee.api.domain.Apply;
import com.mementee.api.domain.Board;
import com.mementee.api.domain.enumtype.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApplyRepositorySub extends JpaRepository<Apply, Long> {

    @Query("SELECT a FROM Apply a WHERE a.sendMember.id = :memberId")
    Page<Apply> findMySendApplyByPage(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT a FROM Apply a WHERE a.receiveMember.id = :memberId")
    Page<Apply> findMyReceiveApplyByPage(@Param("memberId")Long memberId, Pageable pageable);
}
