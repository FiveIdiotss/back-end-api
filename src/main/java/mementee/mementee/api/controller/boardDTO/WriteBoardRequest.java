package mementee.mementee.api.controller.boardDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WriteBoardRequest {
    private String title;
    private String content;
}
