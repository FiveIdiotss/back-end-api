package com.team.mementee.api.dto.memberDTO;

import com.team.mementee.api.domain.enumtype.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateMemberRequest {
    private String email;
    private String name;
    private String password;
    private int year;  //학번
    private Gender gender;

   // private Long schoolId;
    private String schoolName;
    private Long majorId;
}
