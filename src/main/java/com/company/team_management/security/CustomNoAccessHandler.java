package com.company.team_management.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;

public class CustomNoAccessHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        try (Writer writer = response.getWriter()) {
            writer.append("Access not authorized!\n")
                    .append("Please, provide valid Security API key");
            writer.flush();
        }
    }
}
