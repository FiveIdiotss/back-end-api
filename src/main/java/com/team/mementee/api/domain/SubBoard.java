package com.team.mementee.api.domain;

import com.team.mementee.api.domain.enumtype.BoardCategory;
import com.team.mementee.api.domain.enumtype.Platform;
import com.team.mementee.api.domain.enumtype.SubBoardType;
import com.team.mementee.api.dto.subBoardDTO.WriteSubBoardRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SubBoard extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_board_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    private int likeCount;
    private int replyCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardCategory boardCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Platform platform;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubBoardType subBoardType;

    public SubBoard(String title, String content, Member member, BoardCategory boardCategory, SubBoardType subBoardType,  Platform platform) {
        this.title = title;
        this.content = content;
        this.member = member;
        this.likeCount = 0;
        this.replyCount = 0;
        this.boardCategory = boardCategory;
        this.subBoardType = subBoardType;
        this.platform = platform;
    }

    public void modifySubBoard(WriteSubBoardRequest request){
        this.title = request.getTitle();
        this.content = request.getContent();
        this.boardCategory = request.getBoardCategory();
        this.platform = request.getPlatform();
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
