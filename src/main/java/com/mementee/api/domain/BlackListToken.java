package com.mementee.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class BlackListToken {

    @Id
    @GeneratedValue
    @Column(name = "blacklist_token_id")
    private Long id;


    @Column(nullable = false, name = "blacklist_token")
    private String blackListToken;

    public BlackListToken(String blackListToken) {
        this.blackListToken = blackListToken;
    }
}
