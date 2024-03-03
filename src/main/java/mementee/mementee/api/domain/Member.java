package mementee.mementee.api.domain;

import jakarta.persistence.*;
import lombok.*;
import mementee.mementee.api.domain.chat.ChatMessage;
import mementee.mementee.api.domain.chat.ChatRoom;
import mementee.mementee.api.domain.enumtype.Gender;
import mementee.mementee.api.domain.enumtype.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member{

    @Id
    @GeneratedValue
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
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private Major major;

    // chat service 관련 추가
    @OneToMany(mappedBy = "sender")
    @Builder.Default
    private List<ChatRoom> sentChatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "receiver")
    @Builder.Default
    private List<ChatRoom> receivedChatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "sender")
    @Builder.Default
    private List<ChatMessage> sentMessages = new ArrayList<>();


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

    public Member(String email, String name, String password, int year, Gender gender) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.year = year;
        this.gender = gender;
        this.role = Role.USER;
        this.school = school;
        this.major = major;
    }
}
