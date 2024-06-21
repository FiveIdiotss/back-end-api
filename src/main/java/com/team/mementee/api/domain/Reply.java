package com.team.mementee.api.domain;

import com.team.mementee.api.dto.subBoardDTO.ReplyRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Reply {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long id;

    @Lob
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    private LocalDateTime writeTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_board_id")
    private SubBoard subBoard;

    public Reply(String content, Member member, SubBoard subBoard) {
        this.content = content;
        this.writeTime = LocalDateTime.now();
        this.member = member;
        this.subBoard = subBoard;
    }

    public void modifyReply(ReplyRequest request){
        this.content = request.getContent();
        this.writeTime = LocalDateTime.now();
    }
}
