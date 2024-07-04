package com.team.mementee.api.dto.memberDTO;

import com.team.mementee.api.domain.Member;
import com.team.mementee.api.domain.enumtype.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberInfoResponse {
    private Long memberId;
    private String email;
    private String name;
    private int year;
    private Gender gender;

    private String schoolName;
    private String majorName;
    private String memberImageUrl;

    public static MemberInfoResponse createMemberInfoResponse(Member member) {
        return new MemberInfoResponse(member.getId(), member.getEmail(), member.getName(), member.getYear(),
                member.getGender(), member.getSchool().getName(), member.getMajor().getName(), member.getMemberImageUrl());
    }
}
