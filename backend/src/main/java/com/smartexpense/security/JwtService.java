package com.smartexpense.security;

import com.smartexpense.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    public String generateToken(Long userId, String email) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtProperties.getExpirationMs());
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .issuedAt(now)
                .expiration(exp)
                .signWith(signingKey())
                .compact();
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long extractUserId(String token) {
        Claims claims = Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).getPayload();
        return Long.parseLong(claims.getSubject());
    }

    private SecretKey signingKey() {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
