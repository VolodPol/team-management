package com.company.team_management.security.config;

import com.company.team_management.security.JwtAuthenticationFilter;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("development")
class SecurityConfigTest {
    @Autowired
    @Qualifier("secure")
    private SecurityFilterChain chain;

    @Test
    public void testFiltersInChain() {
        List<Filter> filters = chain.getFilters();
        List<Class<?>> filterClasses = filters.stream()
                .map(Object::getClass)
                .collect(Collectors.toList());
        assertTrue(
                filterClasses.containsAll(
                        List.of(
                                SecurityContextHolderFilter.class,
                                LogoutFilter.class,
                                JwtAuthenticationFilter.class,
                                AuthorizationFilter.class,
                                ExceptionTranslationFilter.class
                        )
                )
        );
    }
}