package com.mementee.api.domain;

import com.mementee.api.domain.enumtype.Gender;
import com.mementee.api.domain.enumtype.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "member_image_url")
    private String memberImageUrl;      //프로필 사진

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

    public Member(String email, String name, String password, int year, String defaultMemberImageUrl,
                  Gender gender, School school, Major major) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.year = year;
        this.gender = gender;
        this.role = Role.USER;
        this.school = school;
        this.major = major;
        this.memberImageUrl = defaultMemberImageUrl;
    }

    public Member updateMemberImage(String newMemberImageUrl) {
        this.memberImageUrl = newMemberImageUrl;
        return this;
    }

    public void changePassword(String password){
        this.password = password;
    }
}
