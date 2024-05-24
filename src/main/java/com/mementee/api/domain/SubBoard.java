package com.mementee.api.domain;

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


    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_board_image_id")
    private List<SubBoardImage> subBoardImages = new ArrayList<>();

    public SubBoard(String title, String content, Member member) {
        this.title = title;
        this.content = content;
        this.member = member;
        this.writeTime = LocalDateTime.now();
        this.likeCount = 0;
    }

    public SubBoard(String title, String content, Member member, List<SubBoardImage> subBoardImages) {
        this.title = title;
        this.content = content;
        this.member = member;
        this.subBoardImages = subBoardImages;
        this.writeTime = LocalDateTime.now();
        this.likeCount = 0;
        this.replyCount = 0;
    }

    public void addSubBoardImage(List<SubBoardImage> subBoardImages){
        if (subBoardImages.isEmpty())
            return;
        for (SubBoardImage subBoardImage : subBoardImages){
            this.getSubBoardImages().add(subBoardImage);
        }
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
