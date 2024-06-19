package com.mementee.api.service;

import com.mementee.api.domain.Member;
import com.mementee.api.dto.memberDTO.CustomMemberDetails;
import com.mementee.api.repository.member.MemberRepository;
import com.mementee.exception.notFound.MemberNotFound;
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
