package com.team.mementee.api.dto.memberDTO;

import com.team.mementee.api.domain.School;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SchoolDTO {
    private Long schoolId;
    private String name;

    public static SchoolDTO createSchoolDTO(School school) {
        return new SchoolDTO(school.getId(), school.getName());
    }

    public static List<SchoolDTO> createSchoolDTOs(List<School> schools) {
        return schools.stream()
                .map(SchoolDTO::createSchoolDTO)
                .toList();
    }
}
