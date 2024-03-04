package com.mementee.api.domain;

import com.mementee.api.domain.enumtype.Gender;
import com.mementee.api.domain.enumtype.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
public class Member{

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(length = 45, nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int year;                //학번

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private Major major;

    @OneToMany(mappedBy = "member")
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "sendMember")
    @Column(name = "send_member")
    private List<Apply> sendApplies = new ArrayList<>();

    @OneToMany(mappedBy = "receiveMember")
    @Column(name = "receive_member")
    private List<Apply> receiveApplies = new ArrayList<>();

    //--------------
    @OneToMany(mappedBy = "mentor")
    @Column(name = "mentor")
    private List<Matching> myMentorMatching = new ArrayList<>();

    @OneToMany(mappedBy = "mentee")
    @Column(name = "mentee")
    private List<Matching> myMenteeMatching = new ArrayList<>();

    public Member(String email, String name, String password, int year, Gender gender, School school, Major major) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.year = year;
        this.gender = gender;
        this.role = Role.USER;
        this.school = school;
        this.major = major;
    }

    //임시
    public Member(String email, String name, String password, int year, Gender gender) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.year = year;
        this.gender = gender;
    }

    public void removeSendApplication(Apply apply) {        //보낸 신청 제거
        sendApplies.remove(apply);
    }

    public void removeReceiveApplication(Apply apply) {     //받은 신청 제거
        receiveApplies.remove(apply);
    }

}
