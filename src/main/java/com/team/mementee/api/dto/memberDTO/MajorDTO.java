package com.team.mementee.api.dto.memberDTO;

import com.team.mementee.api.domain.Major;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MajorDTO {
    private Long majorId;
    private String name;
    
    public static List<MajorDTO> createMajorDTOs(List<Major> majors) {
        return majors.stream()
                .map(m -> new MajorDTO(m.getId(), m.getName()))
                .toList();
    }
}
