package com.tiamo.campusmarket.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    // 必须 256 位（32 字节）以上的 key，HS512 需要 512 位
    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private static final long EXPIRE = 1000 * 60 * 60 * 24 * 7; // 7天

    private static final ThreadLocal<Long> USER_HOLDER = new ThreadLocal<>();

    public static String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(KEY)  // 直接用安全生成的 key！！！
                .compact();
    }

    public static Long getUserId(String token) {
        return Long.parseLong(Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
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