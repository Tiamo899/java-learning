package com.tiamo.campusmarket.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET = "tiamo899-campus-market-2026"; // 随便写，越长越好
    private static final long EXPIRE = 1000 * 60 * 60 * 24 * 7; // 7天

    public static String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public static Long getUserId(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        return Long.parseLong(claims.getSubject());
    }
}