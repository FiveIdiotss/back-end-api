package com.team.mementee.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class SubBoardLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_board_like_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "sub_board_id")
    private SubBoard subBoard;

    public SubBoardLike(Member member, SubBoard subBoard) {
        this.member = member;
        this.subBoard = subBoard;
    }
}
