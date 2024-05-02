package com.mementee.api.domain;

import com.mementee.api.domain.enumtype.BoardCategory;
import com.mementee.api.domain.subdomain.UnavailableTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.mementee.api.domain.enumtype.BoardType;
import com.mementee.api.domain.subdomain.ScheduleTime;
import org.hibernate.annotations.ColumnDefault;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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


//    @ElementCollection
//    @Column(name = "available_days", nullable = false)
//    private List<LocalDate> availableDays = new ArrayList<>();              //상담 가능한 요일


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardType boardType;                                            //멘토로써 올리는지, 멘티로써 올리는지

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardCategory boardCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime writeTime;            //작성 시간

    @OneToMany(mappedBy = "board")
    private List<Apply> applies = new ArrayList<>();

    @OneToMany(mappedBy = "board")
    private List<Matching> matchings = new ArrayList<>();

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

    //게시물 사진
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_image_id")
    private List<BoardImage> boardImages = new ArrayList<>();

    public Board(String title, String introduce, String target, String content,
                 int consultTime, BoardCategory boardCategory, BoardType boardType,  Member member,
                 List<ScheduleTime> times, List<DayOfWeek> availableDays, List<BoardImage> boardImages) {
        this.title = title;
        this.introduce = introduce;
        this.target = target;
        this.content = content;
        this.consultTime = consultTime;
        this.times = times;
        this.availableDays = availableDays;
        this.boardCategory = boardCategory;
        this.boardType = boardType;
        this.member = member;
        this.boardImages = boardImages;
        this.writeTime = LocalDateTime.now();
    }

    public Board(String title, String introduce, String target, String content, int consultTime,
                 BoardCategory boardCategory, BoardType boardType,  Member member,
                 List<ScheduleTime> times, List<DayOfWeek> availableDays) {
        this.title = title;
        this.introduce = introduce;
        this.target = target;
        this.content = content;
        this.consultTime = consultTime;
        this.times = times;
        this.availableDays = availableDays;
        this.boardCategory = boardCategory;
        this.boardType = boardType;
        this.member = member;
        this.writeTime = LocalDateTime.now();
    }

    public void addUnavailableTimes(LocalDate date, LocalTime startTime){
        this.getUnavailableTimes().add(new UnavailableTime(date, startTime));
    }

    public void addBoardImage(List<BoardImage> boardImages){
        if (boardImages.isEmpty())
            return;
        for (BoardImage boardImage : boardImages){
            this.getBoardImages().add(boardImage);
        }
    }

    public void modifyBoards(String title, String introduce, String target,
                             String content, int consultTime, BoardCategory boardCategory,
                             BoardType boardType,
                             List<ScheduleTime> times, List<DayOfWeek> availableDays){
        this.title = title;
        this.introduce = introduce;
        this.target = target;
        this.content = content;
        this.consultTime = consultTime;
        this.boardCategory = boardCategory;
        this.boardType = boardType;
        this.times = times;
        this.availableDays = availableDays;
        this.writeTime = LocalDateTime.now();
    }
}
