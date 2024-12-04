package com.team.mementee.api.repository.member;

import com.team.mementee.api.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByEmail(String email);

    @Query("SELECT m FROM Member m " +
            " JOIN FETCH m.school s " +
            " JOIN fetch m.major ma ")
    Optional<Member> findMemberById(@RequestParam("memberId") Long memberId);
}
