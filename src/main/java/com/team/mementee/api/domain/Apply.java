package com.team.mementee.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.team.mementee.api.domain.enumtype.ApplyState;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
public class Apply {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apply_id")
    private Long id;

    @Lob
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(nullable = false)
    private LocalDate date;                     //예약 날짜

    @Column(nullable = false)
    private LocalTime startTime;                //예약시간

    private LocalDateTime applyTime;            //신청 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sendMember")
    private Member sendMember;                  //신청자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiveMember")
    private Member receiveMember;               //신청 받는 자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplyState applyState;              //수락 또는 거절을 눌렀을때 또는 아직 아무것도 누르지 않았을 경우

    public Apply(LocalDate date, LocalTime startTime, Member sendMember, Member receiveMember, Board board, String content) {
        this.content = content;
        this.date = date;
        this.startTime = startTime;
        this.sendMember = sendMember;
        this.receiveMember = receiveMember;
        this.board = board;
        this.applyState = ApplyState.HOLDING;
        this.applyTime = LocalDateTime.now();
    }

    //신청 상황 업데이트
    public void updateState(){
        this.applyState = ApplyState.COMPLETE;
    }

}
