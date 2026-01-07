package com.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 유틸리티 클래스.
 */
@Slf4j
public class JwtUtil {

    private static Key key;
    private static long accessTokenExpiration;
    private static long refreshTokenExpiration;

    /**
     * JWT 설정을 초기화합니다.
     * 외부(Config Server 등)에서 주입받은 값으로 설정합니다.
     *
     * @param secret 시크릿 키
     * @param accessExp Access Token 만료 시간
     * @param refreshExp Refresh Token 만료 시간
     */
    public static void init(String secret, long accessExp, long refreshExp) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
        accessTokenExpiration = accessExp;
        refreshTokenExpiration = refreshExp;
        log.info("JwtUtil initialized with secret key and expirations.");
    }

    /**
     * Access Token 생성.
     *
     * @param userId 사용자 ID
     * @return 생성된 Access Token
     */
    public static String generateAccessToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Refresh Token 생성.
     *
     * @param userId 사용자 ID
     * @return 생성된 Refresh Token
     */
    public static String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰에서 사용자 ID 추출.
     *
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public static Long getUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 토큰 남은 유효시간 조회.
     *
     * @param token JWT 토큰
     * @return 남은 시간 (ms)
     */
    public static Long getExpiration(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            long now = new Date().getTime();
            return (expiration.getTime() - now);
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 토큰 유효성 검증.
     *
     * @param token JWT 토큰
     * @return 유효 여부
     */
    public static boolean validate(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
