package mementee.mementee.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class School {

    @GeneratedValue
    @Id
    @Column(name = "school_id")
    private Long id;

    @Column(length = 45, nullable = false)
    private String name;

    @OneToMany(mappedBy = "school")
    private List<Member> members = new ArrayList<>();

    @OneToMany(mappedBy = "school")
    private List<Major> majors = new ArrayList<>();

    public School(String name) {
        this.name = name;
    }
}