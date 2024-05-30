package com.mementee.api.dto.memberDTO;

import com.mementee.api.domain.Member;
import com.mementee.api.domain.enumtype.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MemberDTO {
    private Long id;
    private String email;
    private String name;
    private int year;                //학번
    private Gender gender;
    private String schoolName;
    private String majorName;

    private String memberImageUrl;        //프로필 사진

    public static MemberDTO createMemberDTO(Member member) {
        return new MemberDTO(member.getId(), member.getEmail(), member.getName(), member.getYear(),
                member.getGender(), member.getSchool().getName(),
                member.getMajor().getName(),
                member.getMemberImageUrl());
    }

    public static List<MemberDTO> createMemberDTOs(List<Member> members) {
        return members.stream()
                .map(MemberDTO::createMemberDTO)
                .toList();
    }
}
