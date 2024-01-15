package com.company.team_management.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("development")
public class JwtServiceTest {
    @Mock
    private UserDetails userDetails;
    @Autowired
    private JwtService service;

    @Value("${app.security.jwt.secret_key}")
    private String secret;
    @Value("${app.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${app.security.jwt.refresh-token.expiration}")
    private long jwtRefreshExpiration;

    @BeforeEach
    void setUp() {
        when(userDetails.getUsername())
                .thenReturn("username");
    }

    @Test
    public void testGenerationOfJwtTokenByUserDetails() {
        String expectedToken = createJwtToken(new HashMap<>(), userDetails, jwtExpiration);
        String actualToken = service.generateJwtToken(userDetails);

        assertEquals(expectedToken, actualToken);
    }

    @Test
    public void testGenerationOfJwtTokenByExtraClaimsAndUserDetails() {
        Map<String, Object> additionalClaims = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            additionalClaims.put(
                    String.format("claim %d", i),
                    String.format("value %d", i)
            );
        }

        String expected = createJwtToken(additionalClaims, userDetails, jwtExpiration);
        String actual = service.generateJwtToken(additionalClaims, userDetails);

        assertEquals(expected, actual);
    }

    @Test
    public void testGenerationOfRefreshToken() {
        String expected = createJwtToken(new HashMap<>(), userDetails, jwtRefreshExpiration);
        String actual = service.generateJwtRefreshToken(userDetails);

        assertEquals(expected, actual);
    }

    @Test
    public void isJwtTokenValidWithWrongUsername_ShouldReturnFalse() {
        String token = createJwtToken(new HashMap<>(), userDetails, jwtExpiration);

        when(userDetails.getUsername()).thenReturn("newUsername");
        assertFalse(service.isJwtTokenValid(token, userDetails));
    }

    @Test
    public void isJwtTokenValidWithExpiredDate_ShouldThrowExpiredJwtException()
            throws Exception {
        String token = createJwtToken(new HashMap<>(), userDetails, 5L);
        Thread.sleep(50L);

        boolean wasThrown = false;
        try {
            service.isJwtTokenValid(token, userDetails);
        } catch (ExpiredJwtException e) {
            wasThrown = true;
        }
        assertTrue(wasThrown);
    }

    @Test
    public void isJwtTokenValid_ShouldReturnTrue() {
        String token = createJwtToken(new HashMap<>(), userDetails, jwtExpiration);
        assertTrue(service.isJwtTokenValid(token, userDetails));
    }

    @Test
    public void testRetrieveClaim() {
        String token = createJwtToken(new HashMap<>(), userDetails, jwtExpiration);
        Claims claims = extractClaimsFromToken(token);

        assertAll(
                () -> assertEquals(claims.getExpiration(), service.retrieveClaim(token, Claims::getExpiration)),
                () -> assertEquals(claims.getId(), service.retrieveClaim(token, Claims::getId)),
                () -> assertEquals(claims.getAudience(), service.retrieveClaim(token, Claims::getAudience)),
                () -> assertEquals(claims.getIssuedAt(), service.retrieveClaim(token, Claims::getIssuedAt)),
                () -> assertEquals(claims.getIssuer(), service.retrieveClaim(token, Claims::getIssuer)),
                () -> assertEquals(claims.getNotBefore(), service.retrieveClaim(token, Claims::getNotBefore))
        );
    }

    @Test
    public void testRetrieveUsername() {
        String token = createJwtToken(new HashMap<>(), userDetails, jwtExpiration);

        assertEquals(extractClaimsFromToken(token).getSubject(), service.retrieveUsername(token));
    }

    private String createJwtToken(Map<String, Object> extraClaims, UserDetails user, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(generateSign(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key generateSign() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    private Claims extractClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(generateSign())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}