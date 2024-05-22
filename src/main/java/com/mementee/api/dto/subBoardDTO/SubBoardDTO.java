package com.mementee.api.dto.subBoardDTO;

import com.mementee.api.domain.Board;
import com.mementee.api.domain.SubBoard;
import com.mementee.api.dto.boardDTO.BoardDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class SubBoardDTO {
    private Long subBoardId;

    private String title;
    private String content;

    private int year;           //작성자 학번
    private String schoolName;  //작성자 학교
    private String majorName;   //작성자 전공

    private Long memberId;      //작성자
    private String imageUrl;
    private String memberName;  //작성자

    private LocalDateTime writeTime;  ////작성 시간

    private boolean isLike;

    public static SubBoardDTO createSubBoardDTO(SubBoard subBoard, boolean isLike) {
        return new SubBoardDTO(subBoard.getId(), subBoard.getTitle(), subBoard.getContent(),
                subBoard.getMember().getYear(), subBoard.getMember().getSchool().getName(), subBoard.getMember().getMajor().getName(),
                subBoard.getMember().getId(), subBoard.getMember().getMemberImageUrl(), subBoard.getMember().getName(), subBoard.getWriteTime(), isLike);
    }

    public static List<SubBoardDTO> createSubBoardDTOs(List<SubBoard> subBoards, boolean isLike) {
        return subBoards.stream()
                .map(b -> createSubBoardDTO(b, isLike))
                .toList();
    }
}


