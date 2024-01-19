package mementee.mementee.api.domain;

import jakarta.persistence.*;
import lombok.*;
import mementee.mementee.api.domain.enumtype.Gender;
import mementee.mementee.api.domain.enumtype.Role;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(length = 45, nullable = false)
    private String name;

    @Column(nullable = false)
    private String pw;

    @Column(nullable = false)
    private int year;                //학번

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private Major major;

    public Member(String email, String name, String pw, int year, Gender gender, School school, Major major) {
        this.email = email;
        this.name = name;
        this.pw = pw;
        this.year = year;
        this.gender = gender;
        this.role = Role.USER;
        this.school = school;
        this.major = major;
    }

//    public static Member toEntity(MemberDTO dto){
//        return Member.builder()
//                .id(dto.getId())
//                .email(dto.getEmail())
//                .name(dto.getName())
//                .pw(dto.getPw())
//                .year(dto.getYear())
//                .score(dto.getScore())
//                .gender(dto.getGender())
//                .school(dto.getSchool())
//                .major(dto.getMajor())
//                .build();
//    }
}
