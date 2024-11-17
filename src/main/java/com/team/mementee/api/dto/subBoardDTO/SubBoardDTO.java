package com.team.mementee.api.dto.subBoardDTO;

import com.team.mementee.api.domain.SubBoard;
import com.team.mementee.api.domain.enumtype.BoardCategory;
import com.team.mementee.api.domain.enumtype.Platform;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class SubBoardDTO {
    private Long subBoardId;
    private BoardCategory boardCategory;

    private String title;
    private String content;

    private int year;                   //작성자 학번
    private String schoolName;          //작성자 학교
    private String majorName;           //작성자 전공

    private Long memberId;              //작성자 id
    private String imageUrl;            //작성자 프로필 사진
    private String memberName;          //작성자 이름

    private LocalDateTime writeTime;    //작성 시간

    private int likeCount;
    private int replyCount;
    private boolean isLike;

    private String representImage;
    private Platform platform;

    public static SubBoardDTO createSubBoardDTO(SubBoard subBoard, boolean isLike, String representImage) {
        return new SubBoardDTO(subBoard.getId(), subBoard.getBoardCategory(),subBoard.getTitle(), subBoard.getContent(),
                subBoard.getMember().getYear(), subBoard.getMember().getSchool().getName(), subBoard.getMember().getMajor().getName(),
                subBoard.getMember().getId(), subBoard.getMember().getMemberImageUrl(), subBoard.getMember().getName(), subBoard.getCreatedAt(),
                subBoard.getLikeCount(), subBoard.getReplyCount(), isLike, representImage, subBoard.getPlatform());
    }
}


