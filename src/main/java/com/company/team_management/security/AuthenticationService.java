package com.company.team_management.security;

import com.company.team_management.controllers.auth.AuthResponse;
import com.company.team_management.controllers.auth.AuthenticationRequest;
import com.company.team_management.controllers.auth.RegistrationRequest;
import com.company.team_management.entities.users.Role;
import com.company.team_management.entities.users.User;
import com.company.team_management.entities.users.token.Token;
import com.company.team_management.entities.users.token.TokenType;
import com.company.team_management.repositories.TokenRepository;
import com.company.team_management.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final JwtService tokenService;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;

    public AuthResponse register(RegistrationRequest request) {
        User newUser = repository.save(
                User.builder()
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .password(encoder.encode(request.getPassword()))
                        .role(Role.USER)
                        .build()
        );
        String userToken = tokenService.generateJwtToken(newUser);
        persistUserToken(newUser, userToken);
        return new AuthResponse(userToken);
    }

    @Transactional
    public AuthResponse authenticate(AuthenticationRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("The user not found"));
        String userToken = tokenService.generateJwtToken(user);
        revokeUserTokens(user);
        persistUserToken(user, userToken);
        return new AuthResponse(userToken);
    }

    private void revokeUserTokens(@NonNull User user) {// can't be null, otherwise an exception will be thrown
        List<Token> validTokens = tokenRepository.findTokensByUserId(user.getId());
        if (!validTokens.isEmpty()) {
            validTokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });//dirty checking
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
}
