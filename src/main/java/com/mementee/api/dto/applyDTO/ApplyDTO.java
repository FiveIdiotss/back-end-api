package com.mementee.api.dto.applyDTO;

import com.mementee.api.domain.Apply;
import com.mementee.api.domain.enumtype.ApplyState;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ApplyDTO {
    private Long applyId;

    private Long boardId;
    private String boardTitle;

    private String content;

    private ApplyState applyState;

    private Long otherMemberId;
    private String otherMemberName;

    private LocalDate date;
    private LocalTime startTime;

    private LocalDateTime applyTime;

    public static List<ApplyDTO> createReceiveApplyDTOs(List<Apply> applies){
        return applies.stream()
                .map(a -> new ApplyDTO(a.getId(), a.getBoard().getId(), a.getBoard().getTitle(), a.getContent(), a.getApplyState(),
                        a.getSendMember().getId(), a.getSendMember().getName(),
                        a.getDate(), a.getStartTime(), a.getApplyTime()))
                .toList();
    }

    public static List<ApplyDTO> createSendApplyDTOs(List<Apply> applies){
        return applies.stream()
                .map(a -> new ApplyDTO(a.getId(), a.getBoard().getId(), a.getBoard().getTitle(), a.getContent(), a.getApplyState(),
                        a.getReceiveMember().getId(), a.getReceiveMember().getName(),
                        a.getDate(), a.getStartTime(), a.getApplyTime()))
                .toList();
    }

}
