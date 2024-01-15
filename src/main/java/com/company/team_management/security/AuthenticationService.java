package com.company.team_management.security;

import com.company.team_management.controllers.auth.AuthResponse;
import com.company.team_management.controllers.auth.AuthenticationRequest;
import com.company.team_management.controllers.auth.RegistrationRequest;
import com.company.team_management.entities.users.User;
import com.company.team_management.entities.users.token.Token;
import com.company.team_management.entities.users.token.TokenType;
import com.company.team_management.repositories.TokenRepository;
import com.company.team_management.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtService tokenService;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;

    public AuthResponse register(RegistrationRequest request) {
        User newUser = userRepository.save(
                User.builder()
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .password(encoder.encode(request.getPassword()))
                        .role(request.getRole())
                        .build()
        );
        String userToken = tokenService.generateJwtToken(newUser);
        String userRefreshToken = tokenService.generateJwtRefreshToken(newUser);
        persistUserToken(newUser, userToken);
        return AuthResponse.builder()
                .token(userToken)
                .refreshToken(userRefreshToken)
                .build();
    }

    @Transactional
    public AuthResponse authenticate(AuthenticationRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("The user not found"));
        String userToken = tokenService.generateJwtToken(user);
        String userRefreshToken = tokenService.generateJwtRefreshToken(user);
        revokeUserTokens(user);
        persistUserToken(user, userToken);
        return AuthResponse.builder()
                .token(userToken)
                .refreshToken(userRefreshToken)
                .build();
    }

    @Transactional
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer")) {
            String refreshToken = header.substring(7);
            String userEmail = tokenService.retrieveUsername(refreshToken);

            if (userEmail != null) {
                User user = userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("The user not found"));

                if (tokenService.isJwtTokenValid(refreshToken, user)) {
                    String newToken = tokenService.generateJwtToken(user);

                    revokeUserTokens(user);
                    persistUserToken(user, newToken);

                    AuthResponse authResponse = AuthResponse.builder()
                            .token(newToken)
                            .refreshToken(refreshToken)
                            .build();

                    new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
                }
            }
        }
    }

    private void persistUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .build();//(expired = revoked = false) - default values in the DB
        tokenRepository.save(token);
    }

    private void revokeUserTokens(@NonNull User user) {
        List<Token> validTokens = tokenRepository.findTokensByUserId(user.getId());
        if (!validTokens.isEmpty()) {
            validTokens.forEach(token -> token.setRevoked(true));
        }
    }
}
