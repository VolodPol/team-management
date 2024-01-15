package com.company.team_management.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${app.security.jwt.secret_key}")
    private String secretKey;
    @Value("${app.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${app.security.jwt.refresh-token.expiration}")
    private long jwtRefreshExpiration;


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
        byte[] bytes = Decoders.BASE64.decode(secretKey);
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
        return buildNewToken(additionalClaims, user, jwtExpiration);
    }

    public String generateJwtRefreshToken(UserDetails user) {
        return buildNewToken(new HashMap<>(), user, jwtRefreshExpiration);
    }

    private String buildNewToken(Map<String, Object> additionalClaims, UserDetails user, long jwtExpiration) {
        return Jwts.builder()
                .setClaims(additionalClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(generateSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
