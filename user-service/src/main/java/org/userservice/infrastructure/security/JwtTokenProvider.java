package org.userservice.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.userservice.common.exception.BusinessException;
import org.userservice.common.exception.ErrorCode;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증을 담당하는 클래스
 */
@Component
@Slf4j
public class JwtTokenProvider {
    
    private final Key key;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    
    public JwtTokenProvider(
            @Value("${security.jwt.secret}") String secretKey,
            @Value("${security.jwt.expiration}") long accessTokenValidityInMilliseconds,
            @Value("${security.jwt.refresh-expiration}") long refreshTokenValidityInMilliseconds) {
        
        // Base64 디코딩 시도, 실패하면 UTF-8 바이트로 처리
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secretKey);
        } catch (Exception e) {
            // Base64 디코딩 실패 시 UTF-8 바이트로 처리
            keyBytes = secretKey.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }
        
        // 키가 충분히 길지 않으면 256비트(32바이트)로 확장
        if (keyBytes.length < 32) {
            byte[] newKeyBytes = new byte[32];
            System.arraycopy(keyBytes, 0, newKeyBytes, 0, Math.min(keyBytes.length, 32));
            keyBytes = newKeyBytes;
        }
        
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
    }
    
    /**
     * Access Token 생성
     */
    public String createAccessToken(Long userId, String username) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds);
        
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("username", username)
                .claim("type", "access")
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(Long userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);
        
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 토큰에서 사용자 ID 추출
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        return Long.parseLong(claims.getSubject());
    }
    
    /**
     * 토큰에서 사용자명 추출
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("username", String.class);
    }
    
    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 토큰 만료 시간 반환
     */
    public LocalDateTime getExpirationFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
    
    /**
     * Access Token 만료 시간 계산
     */
    public LocalDateTime calculateAccessTokenExpiry() {
        return LocalDateTime.now().plusSeconds(accessTokenValidityInMilliseconds / 1000);
    }
    
    /**
     * 토큰에서 Claims 추출
     */
    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN, "토큰이 만료되었습니다.");
        } catch (UnsupportedJwtException e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "지원하지 않는 토큰입니다.");
        } catch (MalformedJwtException e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "잘못된 토큰입니다.");
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "토큰이 없습니다.");
        }
    }
}