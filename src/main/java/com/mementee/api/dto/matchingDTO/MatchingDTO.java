package com.mementee.api.dto.matchingDTO;

import com.mementee.api.domain.Board;
import com.mementee.api.domain.Matching;
import com.mementee.api.domain.enumtype.BoardType;
import com.mementee.api.dto.boardDTO.BoardDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@Data
public class MatchingDTO {
    private Long matchingId;
    private Long applyId;

    private Long otherMemberId;
    private String otherMemberName;

    private LocalDate date;                     // 상담 날짜
    private LocalTime startTime;                // 상담 시작 시간
    private int consultTime;                    // 상담 시간

    public static List<MatchingDTO> createMatchingDTOs(List<Matching> matching, BoardType boardType) {
        if (boardType == BoardType.MENTOR) {
            return matching.stream()
                    .map(m -> new MatchingDTO(m.getId(), m.getApply().getId(),
                            m.getMentor().getId(), m.getMentor().getName(),
                            m.getDate(), m.getStartTime(), m.getConsultTime()))
                    .toList();
        }
        return matching.stream()
                .map(m -> new MatchingDTO(m.getId(), m.getApply().getId(),
                        m.getMentee().getId(), m.getMentee().getName(),
                        m.getDate(), m.getStartTime(), m.getConsultTime()))
                .toList();
    }
}

