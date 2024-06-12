package com.mementee.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class School {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "school_id")
    private Long id;

    @Column(length = 45, nullable = false)
    private String name;

    public School(String name) {
        this.name = name;
    }
}