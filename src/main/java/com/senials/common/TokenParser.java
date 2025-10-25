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

        // JWT 디코딩 로직 (예: jjwt 라이브러리 사용)
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey) // 비밀 키 설정
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userNumber", Integer.class);

    }
}
