package com.mementee.api.dto.subBoardDTO;

import com.mementee.api.domain.BoardImage;
import com.mementee.api.domain.SubBoardImage;
import com.mementee.api.dto.boardDTO.BoardImageDTO;
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
