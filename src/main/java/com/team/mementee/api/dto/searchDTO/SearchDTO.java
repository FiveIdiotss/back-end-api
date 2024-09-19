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

    private List<String> boardTitles;
    private List<String> subBoardTitle_quest;
    private List<String> subBoardTitle_request;
    private List<String> boardContent;
    private List<String> subBoardContent_quest;
    private List<String> subBoardContent_request;


    public static SearchDTO toEntity(List<Board> boards, List<SubBoard> subBoards, List<Board> boards_content, List<SubBoard> subboards_content) {
        return SearchDTO.builder()
                .boardTitles(boards.stream().map(Board::getTitle).toList())
                .subBoardTitle_quest(subBoards.stream().filter(subBoard -> subBoard.getSubBoardType() == SubBoardType.QUEST).map(SubBoard::getTitle).toList())
                .subBoardTitle_request(subBoards.stream().filter(subBoard -> subBoard.getSubBoardType() == SubBoardType.REQUEST).map(SubBoard::getTitle).toList())
                .boardContent(boards_content.stream().map(Board::getTitle).toList())
                .subBoardContent_quest(subboards_content.stream().filter(subBoard -> subBoard.getSubBoardType() == SubBoardType.QUEST).map(SubBoard::getTitle).toList())
                .subBoardContent_request(subboards_content.stream().filter(subBoard -> subBoard.getSubBoardType() == SubBoardType.REQUEST).map(SubBoard::getTitle).toList())
                .build();
    }


}