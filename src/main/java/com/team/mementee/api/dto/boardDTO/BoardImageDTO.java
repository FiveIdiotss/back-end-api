package com.team.mementee.api.dto.boardDTO;

import com.team.mementee.api.domain.BoardImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BoardImageDTO {
    private String boardImageUrl;

    public static List<BoardImageDTO> createBoardImageDTOs(List<BoardImage> boardImages) {
        return boardImages.stream()
                .map(b -> new BoardImageDTO(b.getBoardImageUrl()))
                .toList();
    }
}
