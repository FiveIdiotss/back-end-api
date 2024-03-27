package com.mementee.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardImage {

    @Id
    @GeneratedValue
    @Column(name = "board_image_id")
    private Long id;

    //이미지 url
    @Column(name = "board_image_url")
    private String boardImageUrl;

    public BoardImage(String boardImageUrl) {
        this.boardImageUrl = boardImageUrl;
    }

    public BoardImage updateBoardImage(String newBoardImageUrl) {
        this.boardImageUrl = newBoardImageUrl;
        return this;
    }

}
