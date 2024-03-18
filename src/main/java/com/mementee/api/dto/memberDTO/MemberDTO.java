package com.mementee.api.dto.memberDTO;

import com.mementee.api.domain.enumtype.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;

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
}
