package com.team.mementee.api.domain;

import com.team.mementee.api.domain.enumtype.BoardCategory;
import com.team.mementee.api.domain.subdomain.UnavailableTime;
import com.team.mementee.api.dto.boardDTO.WriteBoardRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.team.mementee.api.domain.enumtype.BoardType;
import com.team.mementee.api.domain.subdomain.ScheduleTime;
import org.hibernate.annotations.ColumnDefault;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Board {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String introduce;

    @Lob
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String target;

    @Lob
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(nullable = false)
    @ColumnDefault("30")
    private int consultTime;                //얼마나 상담할 건지

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardType boardType;                                            //글 작성은 무조건 멘토가

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardCategory boardCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime writeTime;            //작성 시간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private List<DayOfWeek> availableDays = new ArrayList<>();              //상담 가능한 요일

    @ElementCollection
    @CollectionTable(name = "board_available_times", joinColumns = @JoinColumn(name = "board_id"))
    @Column(nullable = false)
    private List<ScheduleTime> times = new ArrayList<>();                  //상담 가능한 시간

    //이미 신청된 시간, 날짜는 예약 하지 못하도록
    @ElementCollection
    @CollectionTable(name = "board_unavailable_times", joinColumns = @JoinColumn(name = "board_id"))
    private List<UnavailableTime> unavailableTimes = new ArrayList<>();

    public Board(String title, String introduce, String target, String content,
                 int consultTime, BoardCategory boardCategory, Member member,
                 List<ScheduleTime> times, List<DayOfWeek> availableDays) {
        this.title = title;
        this.introduce = introduce;
        this.target = target;
        this.content = content;
        this.consultTime = consultTime;
        this.times = times;
        this.availableDays = availableDays;
        this.boardCategory = boardCategory;
        this.member = member;
        this.writeTime = LocalDateTime.now();
        this.boardType = BoardType.MENTOR;
    }

    public void addUnavailableTimes(LocalDate date, LocalTime startTime){
        this.getUnavailableTimes().add(new UnavailableTime(date, startTime));
    }

    public void modifyBoard(WriteBoardRequest request){
        this.title = request.getTitle();
        this.introduce = request.getIntroduce();
        this.target = request.getTarget();
        this.content = request.getContent();
        this.consultTime = request.getConsultTime();
        this.boardCategory = request.getBoardCategory();
        this.times = request.getTimes();
        this.availableDays = request.getAvailableDays();
        this.writeTime = LocalDateTime.now();
    }
}
