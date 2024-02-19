package mementee.mementee.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mementee.mementee.api.domain.enumtype.BoardType;
import mementee.mementee.api.domain.subdomain.UnavailableTime;
import mementee.mementee.api.domain.subdomain.ScheduleTime;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
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
    private int consultTime;                //얼마나 상담할 건지

    @ElementCollection
    @CollectionTable(name = "board_available_times", joinColumns = @JoinColumn(name = "board_id"))
    @Column(nullable = false)
    private List<ScheduleTime> times = new ArrayList<>();                  //상담 가능한 시간

//    @ElementCollection
//    @Column(name = "available_days", nullable = false)
//    private List<LocalDate> availableDays = new ArrayList<>();              //상담 가능한 요일

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private List<DayOfWeek> availableDays = new ArrayList<>();              //상담 가능한 요일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardType boardType;                                            //멘토로써 올리는지, 멘티로써 올리는지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "board")
    private List<Apply> applies = new ArrayList<>();

    @OneToMany(mappedBy = "board")
    private List<Matching> matchings = new ArrayList<>();

    //이미 신청된 시간, 날짜는 예약 하지 못하 도록
    @ElementCollection
    @CollectionTable(name = "board_unavailable_times", joinColumns = @JoinColumn(name = "board_id"))
    @Column(nullable = false)
    private List<UnavailableTime> unavailableTimes = new ArrayList<>();

    public Board(String title, String content, int consultTime, BoardType boardType,  Member member,
                 List<ScheduleTime> times, List<DayOfWeek> availableDays) {
        this.title = title;
        this.content = content;
        this.consultTime = consultTime;
        this.times = times;
        this.availableDays = availableDays;
        this.boardType = boardType;
        this.member = member;
    }

    public void addUnavailableTimes(LocalDate date, LocalTime startTime){
        this.getUnavailableTimes().add(new UnavailableTime(date, startTime));
    }
}
