package mementee.mementee.api.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mementee.mementee.api.controller.dto.MemberDto;
import mementee.mementee.api.controller.dto.MemberRequestDto;
import mementee.mementee.domain.Member;
import mementee.mementee.service.MemberService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/api/member")
    public Long joinMember(@RequestBody @Valid MemberRequestDto memberRequestDto){
        Member member = new Member();
        member.setName(memberRequestDto.getName());

        memberService.join(member);
        return member.getId();
    }


    //모든 회원 조회
    @GetMapping("/api/members")
    public List<MemberDto> findmembers(){
        List<Member> findmembers = memberService.findMembers();
        List<MemberDto> collect = findmembers.stream()
                .map(m -> new MemberDto(m.getName())) //Member entity에서 꺼내와 dto에 넣음
                .collect(Collectors.toList());

        return collect;
    }

}
