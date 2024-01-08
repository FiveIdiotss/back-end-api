package mementee.mementee.service;

import lombok.RequiredArgsConstructor;
import mementee.mementee.domain.Member;
import mementee.mementee.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Long join(Member member) {
        memberRepository.save(member);
        return member.getId();
    }

    //회원 하나 조회
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }
}
