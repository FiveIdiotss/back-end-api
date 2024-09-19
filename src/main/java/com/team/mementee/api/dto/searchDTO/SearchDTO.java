package com.team.mementee.api.dto.searchDTO;

import com.team.mementee.api.domain.Board;
import com.team.mementee.api.domain.SubBoard;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchDTO {

    private List<String> boardTitles;
    private List<String> subBoardTitles;

    public static SearchDTO toEntity(List<Board> boards, List<SubBoard> subBoards) {
        return SearchDTO.builder()
                .boardTitles(boards.stream().map(Board::getTitle).toList())
                .subBoardTitles(subBoards.stream().map(SubBoard::getTitle).toList())
                .build();
    }


}
