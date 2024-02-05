package mementee.mementee.api.controller.memberDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import mementee.mementee.api.domain.enumtype.Gender;

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
