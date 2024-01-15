package com.company.team_management.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

public class TestConfiguration {
    @Bean
    public HandlerMappingIntrospector handlerMappingIntrospector() {
        return Mockito.mock(HandlerMappingIntrospector.class);
    }
}
