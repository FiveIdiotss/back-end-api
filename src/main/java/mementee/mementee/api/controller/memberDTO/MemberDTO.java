package mementee.mementee.api.controller.memberDTO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import mementee.mementee.api.domain.Major;
import mementee.mementee.api.domain.School;
import mementee.mementee.api.domain.enumtype.Gender;
import mementee.mementee.api.domain.enumtype.Role;

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
