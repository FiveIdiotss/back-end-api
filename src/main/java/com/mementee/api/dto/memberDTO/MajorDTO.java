package com.mementee.api.dto.memberDTO;

import com.mementee.api.domain.Major;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MajorDTO {
    private Long majorId;
    private String name;

    public static MajorDTO createMajorDTO(Major major) {
        return new MajorDTO(major.getId(), major.getName());
    }

    public static List<MajorDTO> createMajorDTOs(List<Major> majors) {
        return majors.stream()
                .map(MajorDTO::createMajorDTO)
                .toList();
    }
}
