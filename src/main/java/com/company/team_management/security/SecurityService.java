package com.company.team_management.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

public class SecurityService {
    private static final String AUTH_TOKEN_HEADER = "X-API-KEY";
    private static final String AUTH_TOKEN = "tm07To05ken*";

    public static Authentication getAuthentication(HttpServletRequest request) {
        String key = request.getHeader(AUTH_TOKEN_HEADER);
        if (key == null || !key.equals(AUTH_TOKEN))
            throw new AccessDeniedException("There is no valid key token provided!");

        return new ApiAuthentication(key, AuthorityUtils.NO_AUTHORITIES);
    }
}
