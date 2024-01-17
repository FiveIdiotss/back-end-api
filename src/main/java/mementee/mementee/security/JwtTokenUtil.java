//package mementee.mementee.security;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//
//import java.util.Date;
//
//public class JwtTokenUtil {
//
//
////    Jwt Token 방식을 사용할 때 필요한 기능들을 정리해놓은 클래스
////    새로운 Jwt Token 발급, Jwt Token의 Claim에서 "loginId" 꺼내기, 만료 시간 체크 기능 수행
//
//
//    //JWT Token 발급
//    public static String createToken(String loginEmail, String key, long expireTimeMs){
//        //Claims = Jwt Token에 들어갈 정보
//        //Claim에 loginId를 넣어 줌으로써 나중에 loginId를 꺼낼 수 있음
//        Claims claims = Jwts.claims();
//        claims.put("loginEmail", loginEmail);
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
//                .signWith(SignatureAlgorithm.HS256, key)
//                .compact();
//    }
//
//    //Claims에 loginId 꺼내기
//    public static String getLoginId(String token, String secretKey){
//        return extractClaims(token, secretKey).get("loginEmail").toString();
//    }
//
//    //발급된 Token이 만료 시간이 지났는지 체크
//    public static boolean isExpired(String token, String secretKey){
//        Date expiredDate = extractClaims(token, secretKey).getExpiration();
//        return expiredDate.before(new Date());
//    }
//
//    //SecretKey를 사용해 Token Parsing
//    private static Claims extractClaims(String token, String secretKey){
//        return Jwts.parser().setSigningKey(secretKey).parseClaimsJwt(token).getBody();
//    }
//}
