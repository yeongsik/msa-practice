package com.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

/**
 * JWT 유틸리티 클래스.
 */
public class JwtUtil {

    private static final String SECRET = "my-secret-key-must-be-at-least-256-bits-long";
    private static final long ACCESS_TOKEN_EXPIRATION = 3600000; // 1시간
    private static final long REFRESH_TOKEN_EXPIRATION = 604800000; // 7일
    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

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
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
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
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
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