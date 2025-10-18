package com.github.triprooty.global.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.access-token-expire-ms}") private long accessTokenExpireMs;
    private Key key;

    @PostConstruct void init() { this.key = Keys.hmacShaKeyFor(secret.getBytes()); }

    public String createToken(String email, UUID id) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessTokenExpireMs);

        return Jwts.builder()
                .setSubject(email)
                .claim("uid", id.toString())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validate(String token) {
        try { Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token); return true; }
        catch (JwtException | IllegalArgumentException e) { return false; }
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String getUsername(String token) { return parseClaims(token).getSubject(); }
}
