package mementee.mementee.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mementee.mementee.api.domain.enumtype.BoardType;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Board {

    @Id @GeneratedValue
    @Column(name = "board_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalTime startTime;            // 예약 가능한 시작 시간

    @Column(nullable = false)
    private LocalTime lastTime;              // 예약 가능한 종료 시간

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private List<DayOfWeek> availableDays;  //상담 가능한 요일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardType boardType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "board")
    private List<Application> applications = new ArrayList<>();

    public Board(String title, String content, BoardType boardType,  Member member,
                 LocalTime startTime, LocalTime lastTime, List<DayOfWeek> availableDays) {
        this.title = title;
        this.content = content;
        this.startTime = startTime;
        this.lastTime = lastTime;
        this.availableDays = availableDays;
        this.boardType = boardType;
        this.member = member;
    }
}
