package com.example.TaskManagement.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;
    private final long EXPIRATION = 1000 * 60 * 60*24;

    public String generateToken(CustomUserDetails userDetails) {
        return Jwts.builder()
                .setSubject(String.valueOf(userDetails.getId()))
                .claim("username",userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

    }

    public String extractUserId(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

    public boolean validToken(String token, CustomUserDetails userDetails) {
        String userId = extractUserId(token);
        return String.valueOf(userDetails.getId()).equals(userId) && !isTokenExpired(token);
    }
}
