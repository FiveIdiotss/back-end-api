package mementee.mementee.api.controller.applyDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import mementee.mementee.api.domain.enumtype.ApplyState;

@Data
@AllArgsConstructor
public class ApplyDTO {
    private Long applyId;
    //private BoardDTO boardDTO;
    private Long boardId;
    private String content;

    private ApplyState applyState;
}
