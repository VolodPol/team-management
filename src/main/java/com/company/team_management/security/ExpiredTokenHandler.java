package com.company.team_management.security;


import com.company.team_management.exceptions.jwt_token_expired.TokenExpiredResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
public class ExpiredTokenHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TokenExpiredResponse tokenResponse = TokenExpiredResponse.builder()
                .message("Your jwt token is expired, please refresh your token!")
                .date(new Date())
                .exceptionClass(String.valueOf(authException.getClass()))
                .build();

        setProperties(response);
        mapper.writeValue(response.getOutputStream(), tokenResponse);
    }

    private void setProperties(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }
}
