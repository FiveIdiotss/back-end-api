package mementee.mementee.api.controller.boardDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.aspectj.apache.bcel.generic.LocalVariableGen;

@Getter
@AllArgsConstructor
public class BoardDTO {
    private Long boardId;
    private String title;
    private String content;

    private Long memberId;
    private String memberName;
}
