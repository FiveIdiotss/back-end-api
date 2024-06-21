package com.team.mementee.api.dto.subBoardDTO;

import com.team.mementee.api.domain.SubBoardImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SubBoardImageDTO {
    private String subBoardImageUrl;

    public static List<SubBoardImageDTO> createSubBoardImageDTOs(List<SubBoardImage> subBoardImages) {
        return subBoardImages.stream()
                .map(b -> new SubBoardImageDTO(b.getSubBoardImageUrl()))
                .toList();
    }
}
