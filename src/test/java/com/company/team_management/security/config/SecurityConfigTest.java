package com.company.team_management.security.config;

import com.company.team_management.security.AuthenticationFilter;
import com.company.team_management.security.CustomAuthenticationEntryPoint;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityConfigTest {
    @Autowired
    @Qualifier("filterChain")
    private SecurityFilterChain chain;
    @Autowired
    private AuthenticationEntryPoint authEntryPoint;

    @Test
    public void testFiltersInChain() {
        List<Filter> filters = chain.getFilters();
        List<Class<?>> filterClasses = filters.stream()
                .map(Object::getClass)
                .collect(Collectors.toList());
        assertTrue(
                filterClasses.containsAll(
                        List.of(
                                BasicAuthenticationFilter.class,
                                AuthenticationFilter.class,
                                AuthorizationFilter.class,
                                ExceptionTranslationFilter.class
                        )
                )
        );
    }

    @Test
    public void testIfPresentCustomImplOfAuthEntryPoint() {
        assertTrue(authEntryPoint instanceof CustomAuthenticationEntryPoint);
    }
}