package com.mementee.api.dto.boardDTO;

import com.mementee.api.domain.Board;
import com.mementee.api.domain.enumtype.BoardCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class BoardDTO {

    private Long boardId;
    private BoardCategory boardCategory;

    private String title;
    private String introduce;
    private String target;
    private String content;

    private int year;           //작성자 학번
    private String schoolName;  //작성자 학교
    private String majorName;   //작성자 전공

    private Long memberId;      //작성자
    private String memberName;  //작성자
    private String memberImageUrl;

    private LocalDateTime writeTime;  ////작성 시간

    private boolean isFavorite;

    public static BoardDTO createBoardDTO(Board board, boolean isFavorite) {
        return new BoardDTO(board.getId(), board.getBoardCategory(), board.getTitle(), board.getIntroduce(),
                board.getTarget(), board.getContent(), board.getMember().getYear(),
                board.getMember().getSchool().getName(), board.getMember().getMajor().getName(),
                board.getMember().getId(), board.getMember().getName(), board.getMember().getMemberImageUrl(),
                board.getWriteTime(), isFavorite);
    }

    public static List<BoardDTO> createBoardDTOs(List<Board> boards, boolean isFavorite) {
        return boards.stream()
                .map(b -> createBoardDTO(b, isFavorite))
                .toList();
    }

}
