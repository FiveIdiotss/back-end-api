package com.mementee.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class BoardImage {

    @Id
    @GeneratedValue
    @Column(name = "board_image_id")
    private Long id;

    //이미지 url
    @Column(name = "board_image_url")
    private String boardImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public BoardImage(Board board, String boardImageUrl) {
        this.boardImageUrl = boardImageUrl;
    }
}
