package com.mementee.api.repository;

import com.mementee.api.domain.Apply;
import com.mementee.api.domain.Board;
import com.mementee.api.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplyRepository extends JpaRepository<Apply, Long> {

    //이미 신청한 글인지 체크를 위한 메서드
    Optional<Apply> findApplyBySendMemberAndReceiveMemberAndBoard(Member sendMember, Member receiveMember, Board board);

    //나의 신청 한 목록
    Page<Apply> findAppliesBySendMember(Member sendMember, Pageable pageable);

    //나의 신청 한 목록
    Page<Apply> findAppliesByReceiveMember(Member receiveMember, Pageable pageable);

    List<Apply> findAppliesBySendMember(Member sendMember);

    //나의 신청 한 목록
    List<Apply> findAppliesByReceiveMember(Member receiveMember);

}
