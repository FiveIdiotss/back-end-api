package com.mementee.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Major {
    @Id
    @GeneratedValue
    @Column(name = "major_id")
    private Long id;

    @Column(length = 45, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    public Major(String name, School school) {
        this.name = name;
        this.school = school;
    }
}
