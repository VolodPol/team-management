package com.company.team_management.security;

import com.company.team_management.controllers.ProjectController;
import com.company.team_management.entities.Project;
import com.company.team_management.services.impl.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("development")
class AuthenticationFilterTest {
    private MockMvc mockMvc;
    @Autowired
    private ProjectController controller;
    @MockBean
    private ProjectService mainService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .defaultRequest(get("/company/projects"))
                .alwaysExpect(content().contentType(MediaType.APPLICATION_JSON))
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testRequestWithValidTokenHeaderValue() throws Exception {
        List<Project> foundProjects = List.of();
        when(mainService.findAll()).thenReturn(foundProjects);

        mockMvc.perform(get("/company/projects"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(mainService, times(1)).findAll();
    }

    @Test
    public void testRequestWithNoValidHeader() throws Exception {
        mockMvc.perform(get("/company/projects"))
                .andExpect(status().isOk())
                .andDo(print());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}