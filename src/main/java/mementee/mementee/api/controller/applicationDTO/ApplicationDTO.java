package mementee.mementee.api.controller.applicationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import mementee.mementee.api.controller.boardDTO.BoardDTO;

@Data
@AllArgsConstructor
public class ApplicationDTO {
    private Long applyId;
    //private BoardDTO boardDTO;
    private Long boardId;
    private String content;
}
