package com.tiamo.campusmarket.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {

    // 重点：改成固定字符串！！！再也不会每次启动都变了！
    private static final String SECRET = "campus-market-2025-ti-amo-secret-key-must-be-at-least-32-chars-1234567890";

    private static final long EXPIRE = 1000 * 60 * 60 * 24 * 30; // 30天，开发爽歪歪

    private static final ThreadLocal<Long> USER_HOLDER = new ThreadLocal<>();

    public static String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS512)
                .compact();
    }

    public static Long getUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.valueOf(claims.getSubject());
    }

    public static void setCurrentUser(Long userId) {
        USER_HOLDER.set(userId);
    }

    public static Long getCurrentUser() {
        return USER_HOLDER.get();
    }

    public static void clear() {
        USER_HOLDER.remove();
    }
}