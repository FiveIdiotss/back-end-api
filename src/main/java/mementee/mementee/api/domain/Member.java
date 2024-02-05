package mementee.mementee.api.domain;

import jakarta.persistence.*;
import lombok.*;
import mementee.mementee.api.domain.enumtype.Gender;
import mementee.mementee.api.domain.enumtype.Role;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @Column(nullable = false)
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
    private List<Application> sendApplications = new ArrayList<>();

    @OneToMany(mappedBy = "receiveMember")
    @Column(name = "receive_member")
    private List<Application> receiveApplications = new ArrayList<>();

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

    public void removeSendApplication(Application application) {        //보낸 신청 제거
        sendApplications.remove(application);
    }

    public void removeReceiveApplication(Application application) {     //받은 신청 제거
        receiveApplications.remove(application);
    }

}
