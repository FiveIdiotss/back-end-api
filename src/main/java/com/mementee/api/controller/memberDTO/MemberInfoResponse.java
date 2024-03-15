package com.mementee.api.controller.memberDTO;

import com.mementee.api.domain.enumtype.Gender;
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

    private String majorName;
    private String schoolName;
}
