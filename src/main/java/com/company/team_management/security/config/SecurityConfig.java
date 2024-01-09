package com.company.team_management.security.config;

import com.company.team_management.entities.users.Privilege;
import com.company.team_management.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static com.company.team_management.entities.users.Role.ADMIN;
import static com.company.team_management.entities.users.Role.MANAGER;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final LogoutHandler logoutHandler;
    private final String[] generalEndpoints = new String[] {
            "/company/departments", "/company/department/**",
            "/company/programmers", "/company/programmer/**",
            "/company/projects",    "/company/project/**",
            "/company/tasks",       "/company/task/**"
    };

    @Autowired
    public SecurityConfig(AuthenticationProvider authenticationProvider,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          LogoutHandler logoutHandler) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.logoutHandler = logoutHandler;
    }

    @Bean
    public MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspect) {
        return new MvcRequestMatcher.Builder(introspect);
    }

    @Bean
    public SecurityFilterChain secure(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(customizer -> authorizeRequests(mvc, customizer))

                .sessionManagement(configurer -> configurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/company/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) ->
                                SecurityContextHolder.clearContext()));

        return http.build();
    }

    private void authorizeRequests(MvcRequestMatcher.Builder mvc, AuthorizeHttpRequestsConfigurer<HttpSecurity>
            .AuthorizationManagerRequestMatcherRegistry customizer) {
        customizer
                .requestMatchers(mvc.pattern("/company/auth/**"))
                .permitAll()

                .requestMatchers(mvc.pattern("/company/manage/**")).hasAnyRole(MANAGER.name(), ADMIN.name())

                .requestMatchers(POST, generalEndpoints).hasRole(ADMIN.name())
                .requestMatchers(POST, generalEndpoints).hasAuthority(Privilege.ADMIN_CREATE.name())

                .requestMatchers(PUT, generalEndpoints).hasRole(ADMIN.name())
                .requestMatchers(PUT, generalEndpoints).hasAuthority(Privilege.ADMIN_UPDATE.name())

                .requestMatchers(DELETE, generalEndpoints).hasRole(ADMIN.name())
                .requestMatchers(DELETE, generalEndpoints).hasAuthority(Privilege.ADMIN_DELETE.name())

                .anyRequest()
                .authenticated();
    }
}