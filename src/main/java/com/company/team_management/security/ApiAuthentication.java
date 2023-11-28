package com.company.team_management.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class ApiAuthentication extends AbstractAuthenticationToken {
    private final String apiKey;

    public ApiAuthentication(String key, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.apiKey = key;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return apiKey;
    }
}
