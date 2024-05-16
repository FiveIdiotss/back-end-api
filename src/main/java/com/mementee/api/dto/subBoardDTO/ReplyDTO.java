package com.mementee.api.dto.subBoardDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReplyDTO {
    private Long replyId;

    private Long memberId;
    private String imageUrl;
    private String memberName;
    private String majorName;

    private LocalDateTime localDateTime;
    private String content;
}
