package mementee.mementee.api.controller.memberDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mementee.mementee.api.domain.enumtype.Gender;

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
