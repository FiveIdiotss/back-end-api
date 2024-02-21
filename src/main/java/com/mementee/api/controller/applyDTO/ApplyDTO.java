package com.mementee.api.controller.applyDTO;

import com.mementee.api.domain.enumtype.ApplyState;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplyDTO {
    private Long applyId;
    //private BoardDTO boardDTO;
    private Long boardId;
    private String content;

    private ApplyState applyState;
}
