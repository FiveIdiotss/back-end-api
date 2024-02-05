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

    private int year;           //작성자 학번
    private String schoolName;  //작성자 학교
    private String majorName;   //작성자 전공

    private Long memberId;      //작성자
    private String memberName;  //작성자
}
