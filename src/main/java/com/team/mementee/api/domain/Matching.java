package com.team.mementee.api.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Matching extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matching_id")
    private Long id;

    @Column(nullable = false)
    private LocalDate date;                     // 상담 날짜

    @Column(nullable = false)
    private LocalTime startTime;                 // 상담 시작 시간

    @Column(nullable = false)
    private int consultTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Apply apply;

    //apply 의 receiveMember 의 타입에 따라 멘토, 멘티 저장

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor")
    private Member mentor;                       // 멘토

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee")
    private Member mentee;                     // 매칭 요청을 받은 회원

    public Matching(LocalDate date, LocalTime startTime, int consultTime,
                    Board board, Apply apply, Member mentor, Member mentee) {
        this.date = date;
        this.startTime = startTime;
        this.consultTime = consultTime;
        this.board = board;
        this.apply = apply;
        this.mentor = mentor;
        this.mentee = mentee;
    }

    public void extendConsultTime(){
        this.consultTime = this.consultTime + 30;

    }
}