package com.mementee.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class BlackListToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blacklist_token_id")
    private Long id;


    @Column(nullable = false, name = "blacklist_token")
    private String blackListToken;

    public BlackListToken(String blackListToken) {
        this.blackListToken = blackListToken;
    }
}
