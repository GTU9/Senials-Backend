package com.senials.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenParser {

    @Value("${jwt.secret}")
    private String secretKey;


    // JWT에서 userNumber를 추출하는 메서드
    public int extractUserNumberFromToken(String token) {
        // "null" 문자열 또는 빈 토큰 방어
        if (token == null || token.equals("null") || token.trim().isEmpty()) {
            throw new RuntimeException("토큰이 없습니다.");
        }

        String normalizedToken = normalizeToken(token);

        // JWT 디코딩 로직 (예: jjwt 라이브러리 사용)
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey) // 비밀 키 설정
                .parseClaimsJws(normalizedToken)
                .getBody();
        return claims.get("userNumber", Integer.class);

    }

    private String normalizeToken(String token) {
        String normalized = token.trim();

        if (normalized.startsWith("Bearer ")) {
            normalized = normalized.substring(7).trim();
        }

        // JWT는 점(.)으로 구분된 3파트여야 함
        String[] parts = normalized.split("\\.");
        if (parts.length != 3) {
            throw new RuntimeException("유효하지 않은 JWT 형식입니다.");
        }

        return normalized;
    }
}
