package com.team.mementee.api.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SubBoardImage extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
