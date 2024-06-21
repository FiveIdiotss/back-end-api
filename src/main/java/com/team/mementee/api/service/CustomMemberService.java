package com.team.mementee.api.service;

import com.team.mementee.api.domain.Member;
import com.team.mementee.api.dto.memberDTO.CustomMemberDetails;
import com.team.mementee.api.repository.member.MemberRepository;
import com.team.mementee.exception.notFound.MemberNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomMemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    @Override
    public CustomMemberDetails loadUserByUsername(String email) {
        Optional<Member> member = memberRepository.findMemberByEmail(email);
        if(member.isEmpty())
            throw new MemberNotFound();

        return new CustomMemberDetails(member.get());
    }
}
