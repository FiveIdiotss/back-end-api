package com.team.mementee.api.dto.boardDTO;

import com.team.mementee.api.domain.Board;
import com.team.mementee.api.domain.enumtype.BoardCategory;
import com.team.mementee.api.domain.enumtype.Platform;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {

    private Long boardId;
    private BoardCategory boardCategory;

    private String title;
    private String introduce;
    private String target;
    private String content;

    private int year;                   //작성자 학번
    private String schoolName;          //작성자 학교
    private String majorName;           //작성자 전공

    private Long memberId;              //작성자 id
    private String memberName;          //작성자 이름
    private String memberImageUrl;      //작성자 프로필 사진

    private LocalDateTime writeTime;    ///작성 시간

    private boolean isFavorite;

    private String representImage;
    private Platform platform;


    public static BoardDTO createBoardDTO(Board board, boolean isFavorite, String representImage) {
        return new BoardDTO(board.getId(), board.getBoardCategory(), board.getTitle(), board.getIntroduce(),
                board.getTarget(), board.getContent(), board.getMember().getYear(),
                board.getMember().getSchool().getName(), board.getMember().getMajor().getName(),
                board.getMember().getId(), board.getMember().getName(), board.getMember().getMemberImageUrl(),
                board.getCreatedAt(), isFavorite, representImage, board.getPlatform());
    }
}
