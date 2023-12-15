package com.company.team_management.security;

import com.company.team_management.controllers.auth.AuthResponse;
import com.company.team_management.controllers.auth.AuthenticationRequest;
import com.company.team_management.controllers.auth.RegistrationRequest;
import com.company.team_management.entities.users.Role;
import com.company.team_management.entities.users.User;
import com.company.team_management.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final JwtService tokenService;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;

    public AuthResponse register(RegistrationRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);
        String userToken = tokenService.generateJwtToken(user);
        return new AuthResponse(userToken);
    }

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
        return new AuthResponse(userToken);
    }
}
