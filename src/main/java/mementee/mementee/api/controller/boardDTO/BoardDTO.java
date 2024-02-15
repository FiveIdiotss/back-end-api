package mementee.mementee.api.controller.boardDTO;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mementee.mementee.api.domain.enumtype.BoardType;
import org.aspectj.apache.bcel.generic.LocalVariableGen;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class BoardDTO {
    private Long boardId;
    private BoardType boardType;
    private String title;
    private String content;

    private int year;           //작성자 학번
    private String schoolName;  //작성자 학교
    private String majorName;   //작성자 전공

    private Long memberId;      //작성자
    private String memberName;  //작성자
}
