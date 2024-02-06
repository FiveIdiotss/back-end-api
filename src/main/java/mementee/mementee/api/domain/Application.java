package mementee.mementee.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id @GeneratedValue
    @Column(name = "application_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDate date;                     //예약 날짜

    @Column(nullable = false)
    private LocalTime time;                     //예약 시작 시간 (30분 단위)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sendMember")
    private Member sendMember;                  //신청자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiveMember")
    private Member receiveMember;               //신청 받는 자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public Application(LocalDate date, LocalTime time, Member sendMember, Member receiveMember, Board board, String content) {
        this.content = content;
        this.date = date;
        this.time = time;
        this.sendMember = sendMember;
        this.receiveMember = receiveMember;
        this.board = board;
    }
}
