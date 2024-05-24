package com.mementee.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubBoardImage {

    @Id
    @GeneratedValue
    @Column(name = "sub_board_image_id")
    private Long id;

    //이미지 url
    @Column(name = "sub_board_image_url")
    private String subBoardImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_board_id")
    private SubBoard subBoard;

    public SubBoardImage(String subBoardImageUrl, SubBoard subBoard) {
        this.subBoardImageUrl = subBoardImageUrl;
        this.subBoard = subBoard;
    }
}
