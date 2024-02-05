package mementee.mementee.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mementee.mementee.api.domain.RefreshToken;
import mementee.mementee.api.repository.RefreshTokenRepository;
import mementee.mementee.api.service.MemberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private static final long ACCESS_TIME = 60 * 60 * 1000L;
    private static final long REFRESH_TIME = 7 * 24 * 60 * 60 * 1000L;        //일주일 168 시간


    //token을 통해 사용자 email 조회
    public static String getMemberEmail(String token, String secretKey){
        //System.out.println("여기 token = " + token);
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("email", String.class);
    }

    //토큰 만료 되었는지
    public static boolean isExpired(String token, String secretKey){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().getExpiration().before(new Date());
    }


    //access 토큰 발행
    public static String createAccessToken(String email, String secretKey){
        Claims claims = Jwts.claims();  //일종의 Map
        claims.put("email", email);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    //refresh 토큰 발행
    public static String createRefreshToken(String secretKey){
        Claims claims = Jwts.claims();  //일종의 Map

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

}
