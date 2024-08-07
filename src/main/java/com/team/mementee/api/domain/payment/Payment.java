package com.team.mementee.api.domain.payment;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(nullable = false)
    private int price;
}
