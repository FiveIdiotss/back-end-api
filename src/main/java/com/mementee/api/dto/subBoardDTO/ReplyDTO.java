package com.mementee.api.dto.subBoardDTO;

import com.mementee.api.domain.Apply;
import com.mementee.api.domain.Reply;
import com.mementee.api.dto.applyDTO.ApplyDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public static List<ReplyDTO> createReplyDTOs(List<Reply> replies){
        return replies.stream()
                .map(r -> new ReplyDTO(r.getId(), r.getMember().getId(), r.getMember().getMemberImageUrl(),
                        r.getMember().getName(), r.getMember().getMajor().getName(), r.getWriteTime(), r.getContent()))
                        .collect(Collectors.toList());
    }
}
