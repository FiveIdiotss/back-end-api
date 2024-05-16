package com.mementee.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reply {

    @Id @GeneratedValue
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
}
