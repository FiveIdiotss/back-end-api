package com.mementee.api.domain;

import com.mementee.api.domain.enumtype.BoardCategory;
import com.mementee.api.domain.enumtype.SubBoardType;
import com.mementee.api.dto.subBoardDTO.WriteSubBoardRequest;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class SubBoard {

    @Id
    @GeneratedValue
    @Column(name = "sub_board_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    private int likeCount;
    private int replyCount;

    private LocalDateTime writeTime;            //작성 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private BoardCategory boardCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubBoardType subBoardType;

    public SubBoard(String title, String content, Member member, BoardCategory boardCategory, SubBoardType subBoardType) {
        this.title = title;
        this.content = content;
        this.member = member;
        this.writeTime = LocalDateTime.now();
        this.likeCount = 0;
        this.replyCount = 0;
        this.boardCategory = boardCategory;
        this.subBoardType = subBoardType;
    }

    public void modifySubBoard(WriteSubBoardRequest request){
        this.title = request.getTitle();
        this.content = request.getContent();
    }

    public void plusLikeCount(){
        likeCount++;
    }

    public void minusLikeCount(){
        likeCount--;
    }

    public void plusReplyCount(){
        replyCount++;
    }

    public void minusReplyCount(){
        replyCount--;
    }
}
