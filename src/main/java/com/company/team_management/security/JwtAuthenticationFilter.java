package com.company.team_management.security;

import com.company.team_management.entities.users.token.Token;
import com.company.team_management.repositories.TokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private String jwtToken;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            authenticateViaToken(request);
        } catch (ExpiredJwtException exception) {
            updateExpiredToken();
        }
        filterChain.doFilter(request, response);
    }

    private void authenticateViaToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer")) {
            jwtToken = header.substring(7);
            String userEmail = jwtService.retrieveUsername(jwtToken);

            if (userEmail != null) {
                UserDetails user = userDetailsService.loadUserByUsername(userEmail);
                boolean isValid = jwtService.isJwtTokenValid(jwtToken, user);
                boolean notRevokedAndExpired = tokenRepository.findByToken(jwtToken)
                        .map(token -> !token.isRevoked() && !token.isExpired())
                        .orElse(false);

                if (isValid && notRevokedAndExpired) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
    }

    private void updateExpiredToken() {
        Token found = tokenRepository.findByToken(jwtToken)
                .orElseGet(Token::new);
        found.setExpired(true);
        tokenRepository.save(found);
    }
}