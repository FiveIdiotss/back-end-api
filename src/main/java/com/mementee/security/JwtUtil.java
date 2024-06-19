package com.mementee.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {
    private static final long ACCESS_TIME = 60 * 60 * 1000L;                  //1시간
    private static final long REFRESH_TIME = 7 * 24 * 60 * 60 * 1000L;        //일주일 168 시간

    @Value("${spring.jwt.secret}")
    private String secretKey;

    private static String staticSecretKey;

    @PostConstruct
    public void init() {
        staticSecretKey = secretKey;
    }


    //accessToken을 통해 사용자 email 조회, 이 부분에서 토큰 만료되면 ExpiredJwtException 발생
    public static String getMemberEmail(String token){
        //parseClaimsJws를 통해 토큰 만료 예외 발생 여부 검사
        return Jwts.parser().setSigningKey(staticSecretKey).parseClaimsJws(token)
                .getBody().get("email", String.class);
    }

    //access 토큰 발행
    public static String createAccessToken(String email){
        Claims claims = Jwts.claims();  //일종의 Map
        claims.put("email", email);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TIME))
                .signWith(SignatureAlgorithm.HS256, staticSecretKey)
                .compact();
    }

    //refresh 토큰 발행
    public static String createRefreshToken(){
        Claims claims = Jwts.claims();  //일종의 Map

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TIME))
                .signWith(SignatureAlgorithm.HS256, staticSecretKey)
                .compact();
    }

    //토큰 만료 되었는지
    public static boolean isExpired(String token, String secretKey){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().getExpiration().before(new Date());
    }

    public static boolean validateToken(String token, String secretKey){
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e){
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e){
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e){
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e){
            log.info("JWT claims Empty", e);
        }
        return false;
    }
}
