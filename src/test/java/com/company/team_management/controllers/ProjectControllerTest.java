package com.company.team_management.controllers;

import com.company.team_management.entities.users.Role;
import com.company.team_management.mapper.ProjectMapper;
import com.company.team_management.services.StatisticsService;
import com.company.team_management.utils.test_data_provider.ProjectProvider;
import com.company.team_management.utils.test_data_provider.TestEntityProvider;
import com.company.team_management.utils.TestUtils;
import com.company.team_management.dto.ProjectDTO;
import com.company.team_management.entities.Project;
import com.company.team_management.services.impl.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("development")
class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProjectService service;
    @MockBean
    private StatisticsService statService;
    @Autowired
    private ProjectMapper mapper;
    private Project project;
    private ProjectDTO dto;
    private final TestEntityProvider<Project> entityProvider = new ProjectProvider();

    @BeforeEach
    public void setUp() {
        project = entityProvider.generateEntity();
        project.setId(TestUtils.generateId());
        dto = mapper.entityToDTO(project);
    }

    @Test
    public void fetchAllProjects() throws Exception {
        List<Project> projects = List.of(project);
        List<ProjectDTO> dtoList = mapper.collectionToDTO(projects);
        when(service.findAll()).thenReturn(projects);

        mockMvc.perform(get("/company/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(dtoList))
                        .with(user("user").roles(Role.USER.name())))
                .andExpectAll(
                        status().isOk(),
                        content().json(TestUtils.objectToJsonString(dtoList))
                );
        verify(service, times(1)).findAll();
    }

    @Test
    public void findProjectById() throws Exception {
        when(service.findById(project.getId())).thenReturn(project);

        mockMvc.perform(get("/company/project/{id}", project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(dto))
                        .with(user("user").roles(Role.USER.name())))
                .andExpectAll(
                        status().isFound(),
                        content().json(TestUtils.objectToJsonString(dto))
                );
        verify(service, times(1)).findById(project.getId());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void addNewProject() throws Exception {
        when(service.save(project)).thenReturn(project);

        mockMvc.perform(post("/company/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(dto)))
                .andExpectAll(
                        content().json(TestUtils.objectToJsonString(dto)),
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION)
                );
        verify(service, times(1)).save(project);
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void deleteExistingProjectById() throws Exception {
        doNothing().when(service).deleteById(project.getId());

        mockMvc.perform(delete("/company/project/{id}", project.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        content().string("Successfully deleted!"),
                        status().isNoContent()
                );
        verify(service, times(1)).deleteById(project.getId());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void updateExistingProject() throws Exception {
        Project toUpdate = entityProvider.generateEntity();
        toUpdate.setId(project.getId());
        ProjectDTO updatedDto = mapper.entityToDTO(toUpdate);

        when(service.updateById(toUpdate.getId(), toUpdate))
                .thenReturn(toUpdate);

        mockMvc.perform(put("/company/project/{id}", project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(updatedDto)))
                .andExpectAll(
                        content().json(TestUtils.objectToJsonString(updatedDto)),
                        status().isOk()
                );
        verify(service, times(1)).updateById(toUpdate.getId(), toUpdate);
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void testGetProjectWithinBudget() throws Exception {
        List<Project> projects = entityProvider.generateEntityList();
        long maxBudget = projects.stream()
                .mapToLong(Project::getBudget)
                .max()
                .orElse(0L);

        when(statService.getProjectsWithInfoWithinBudget(0L, maxBudget)).thenReturn(projects);
        mockMvc.perform(get("/company/projects/budget")
                        .param("lower", String.valueOf(0L))
                        .param("upper", String.valueOf(maxBudget)))
                .andExpect(content().json(TestUtils.objectToJsonString(projects)))
                .andExpect(status().isOk());
        verify(statService, times(1)).getProjectsWithInfoWithinBudget(0L, maxBudget);
    }
}
