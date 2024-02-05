package mementee.mementee.api.controller.boardDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import mementee.mementee.api.domain.enumtype.BoardType;

@Data
@AllArgsConstructor
public class WriteBoardRequest {
    private String title;
    private String content;
    private BoardType boardType;
}
