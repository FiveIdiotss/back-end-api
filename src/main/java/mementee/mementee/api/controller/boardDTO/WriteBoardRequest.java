package mementee.mementee.api.controller.boardDTO;

import lombok.Data;

@Data
public class WriteBoardRequest {
    private String title;
    private String content;
}
