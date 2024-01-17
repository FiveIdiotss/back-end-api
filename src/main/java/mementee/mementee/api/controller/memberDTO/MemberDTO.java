package mementee.mementee.api.controller.memberDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import mementee.mementee.domain.Major;
import mementee.mementee.domain.School;
import mementee.mementee.domain.enumtype.Gender;

@Data
@AllArgsConstructor
public class MemberDTO {
    private String name;
}
