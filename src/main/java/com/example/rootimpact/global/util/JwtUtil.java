package com.example.rootimpact.global.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // ✅ 32자리 이상 비밀키 (보안 강화를 위해 환경 변수로 관리하는 것이 좋음)
    private static final String SECRET = "MySuperSecretKeyForJWTMySuperSecretKeyForJWT";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // ✅ 30일 동안 유효한 토큰 설정
    private static final long EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 30; // 30일 (밀리초 단위)

    /**
     * ✅ JWT 토큰 생성 (static 제거)
     */
    public String generateToken(String email) {
        long now = System.currentTimeMillis(); // 현재 시간
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + EXPIRATION_TIME); // ✅ 30일 후 만료

        return Jwts.builder()
                .setSubject(email) // ✅ 사용자 이메일
                .setIssuedAt(issuedAt) // ✅ 발급 시간
                .setExpiration(expiration) // ✅ 만료 시간 (30일 후)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // ✅ HMAC SHA256 알고리즘 사용
                .compact();
    }

    /**
     * ✅ JWT 토큰에서 사용자 정보(이메일) 추출
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * ✅ JWT 토큰에서 특정 클레임 추출
     */
    public <T> T extractClaim(String token, ClaimsResolver<T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.resolve(claims);
    }

    /**
     * ✅ JWT 토큰에서 모든 클레임 추출
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * ✅ JWT 토큰 만료 여부 확인
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * ✅ JWT 토큰 만료 시간 추출
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * ✅ JWT 토큰 유효성 검증
     */
    public Boolean validateToken(String token, String email) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(email) && !isTokenExpired(token));
    }

    /**
     * ✅ 사용자 정의 인터페이스 (Claims를 추출하기 위한 함수형 인터페이스)
     */
    @FunctionalInterface
    public interface ClaimsResolver<T> {
        T resolve(Claims claims);
    }
}