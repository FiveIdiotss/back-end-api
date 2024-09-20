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

    private List<String> boards_title;
    private List<String> subBoards_quest_title;
    private List<String> subBoards_request_title;
    private List<String> boards_content;
    private List<String> subBoards_quest_content;
    private List<String> subBoards_request_content;


    public static SearchDTO toEntity(List<Board> boards_title, List<SubBoard> subBoards_title, List<Board> boards_content, List<SubBoard> subBoards_content) {
        return SearchDTO.builder()
                .boards_title(boards_title.stream().map(Board::getTitle).toList())
                .subBoards_quest_title(subBoards_title.stream().filter(subBoard -> subBoard.getSubBoardType() == SubBoardType.QUEST).map(SubBoard::getTitle).toList())
                .subBoards_request_title(subBoards_title.stream().filter(subBoard -> subBoard.getSubBoardType() == SubBoardType.REQUEST).map(SubBoard::getTitle).toList())
                .boards_content(boards_content.stream().map(Board::getTitle).toList())
                .subBoards_quest_content(subBoards_content.stream().filter(subBoard -> subBoard.getSubBoardType() == SubBoardType.REQUEST).map(SubBoard::getTitle).toList())
                .subBoards_request_content(subBoards_content.stream().filter(subBoard -> subBoard.getSubBoardType() == SubBoardType.REQUEST).map(SubBoard::getTitle).toList())
                .build();
    }


}
