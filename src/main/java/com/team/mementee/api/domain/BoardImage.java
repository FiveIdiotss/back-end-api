package com.team.mementee.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class BoardImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_image_id")
    private Long id;

    //이미지 url
    @Column(name = "board_image_url")
    private String boardImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public BoardImage(Board board, String boardImageUrl) {
        this.board = board;
        this.boardImageUrl = boardImageUrl;
    }
}
