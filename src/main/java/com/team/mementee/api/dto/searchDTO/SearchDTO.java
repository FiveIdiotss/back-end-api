package com.team.mementee.api.dto.searchDTO;

import com.team.mementee.api.domain.Board;
import com.team.mementee.api.domain.SubBoard;
import com.team.mementee.api.domain.enumtype.SubBoardType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchDTO {

    private List<String> boards;
    private List<String> subBoards_quest;
    private List<String> subBoards_request;


    public static SearchDTO toEntity(List<Board> boards, List<SubBoard> subBoards) {
        return SearchDTO.builder()
                .boards(boards.stream().map(Board::getTitle).toList())
                .subBoards_quest(subBoards.stream().filter(subBoard -> subBoard.getSubBoardType() == SubBoardType.QUEST).map(SubBoard::getTitle).toList())
                .subBoards_request(subBoards.stream().filter(subBoard -> subBoard.getSubBoardType() == SubBoardType.REQUEST).map(SubBoard::getTitle).toList())
                .build();
    }


}
