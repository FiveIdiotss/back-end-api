package mementee.mementee.api.controller.memberDTO;

import lombok.Data;
import mementee.mementee.api.domain.enumtype.Gender;

@Data
public class CreateMemberRequest {
    private String email;
    private String name;
    private String pw;
    private int year;  //학번
    private Gender gender;

   // private Long schoolId;
    private String schoolName;
    private Long majorId;
}
