package mementee.mementee.api.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue
    @Column(name = "refresh_token_id")
    private Long id;

    private String refreshToken;

    private String email;
    public RefreshToken(String refreshToken, String email) {
        this.refreshToken = refreshToken;
        this.email = email;
    }

    public RefreshToken updateToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }
    public boolean checkMatchRefreshToken(String refreshToken){
        return this.refreshToken.equals(refreshToken);
    }
}
