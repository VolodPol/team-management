package com.company.team_management.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET = "dd98deec25c89f03e4e12b984f8481c940f1a1dcfe76201a54c949fa82992e84";

    public String retrieveUsername(String jwtToken) {
        return retrieveClaim(jwtToken, Claims::getSubject);
    }

    public <T> T retrieveClaim(String token, Function<Claims, T> retriever) {
        Claims claims = retrieveAllClaims(token);
        return retriever.apply(claims);
    }

    private Claims retrieveAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(generateSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key generateSigningKey() {
        byte[] bytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(bytes);
    }

    public boolean isJwtTokenValid(String token, UserDetails user) {
        String userEmail = user.getUsername();
        return userEmail.equals(retrieveUsername(token)) && !isJwtTokenExpired(token);
    }

    private boolean isJwtTokenExpired(String token) {
        return retrieveClaim(token, Claims::getExpiration).before(new Date());
    }

    public String generateJwtToken(UserDetails user) {
        return generateJwtToken(new HashMap<>(), user);
    }

    public String generateJwtToken(Map<String, Object> additionalClaims, UserDetails user) {
        return Jwts.builder()
                .setClaims(additionalClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(generateSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
