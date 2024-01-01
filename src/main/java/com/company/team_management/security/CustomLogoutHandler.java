package com.company.team_management.security;

import com.company.team_management.repositories.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {
    private final TokenRepository tokenRepository;
    @Transactional
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer")) {
            String jwtToken = header.substring(7);
            invalidateToken(jwtToken);
        }
    }

    private void invalidateToken(String jwtToken) {//auto-update due to dirty checking
        tokenRepository.findByToken(jwtToken)
                .ifPresent(token -> {
                    token.setExpired(true);
                    token.setRevoked(true);
                });
    }
}
