package mementee.mementee.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id @GeneratedValue
    @Column(name = "application_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sendMember")
    private Member sendMember;                  //신청자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiveMember")
    private Member receiveMember;               //신청 받는 자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public Application(Member sendMember, Member receiveMember, Board board, String content) {
        this.sendMember =sendMember;
        this.receiveMember = receiveMember;
        this.board = board;
        this.content = content;
    }

}
