package mementee.mementee.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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