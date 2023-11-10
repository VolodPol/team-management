package com.company.team_management.controllers;

import com.company.team_management.utils.test_data_provider.ProjectProvider;
import com.company.team_management.utils.test_data_provider.TestEntityProvider;
import com.company.team_management.utils.TestUtils;
import com.company.team_management.dto.ProjectDTO;
import com.company.team_management.dto.mapper.impl.ProjectMapper;
import com.company.team_management.entities.Project;
import com.company.team_management.exceptions.ErrorResponse;
import com.company.team_management.exceptions.no_such.NoSuchProjectException;
import com.company.team_management.exceptions.already_exists.ProjectAlreadyExistsException;
import com.company.team_management.services.impl.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(ProjectController.class)
@ComponentScan(basePackages = "com.company.team_management.dto.mapper")
class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProjectService service;
    @Autowired
    private ProjectMapper mapper;
    private Project project;
    private ProjectDTO dto;
    private final TestEntityProvider<Project> entityProvider = new ProjectProvider();

    @BeforeEach
    public void setUp() {
        project = entityProvider.generateEntity();
        project.setId(TestUtils.generateId());
        dto = mapper.toDto(project);
    }

    @Test
    public void fetchAllProjects() throws Exception {
        List<Project> projects = List.of(project);
        List<ProjectDTO> dtoList = mapper.collectionToDto(projects);
        when(service.findAll()).thenReturn(projects);

        mockMvc.perform(get("/company/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(dtoList)))
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
                        .content(TestUtils.objectToJsonString(dto)))
                .andExpectAll(
                        status().isFound(),
                        content().json(TestUtils.objectToJsonString(dto))
                );
        verify(service, times(1)).findById(project.getId());
    }

    @Test
    public void addNewProject() throws Exception {
        when(service.save(project)).thenReturn(project);

        mockMvc.perform(post("/company/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(dto)))
                .andExpectAll(
                        content().json(TestUtils.objectToJsonString(dto)),
                        status().isCreated()
                );
        verify(service, times(1)).save(project);
    }

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

    @Test
    public void updateExistingProject() throws Exception {
        Project toUpdate = entityProvider.generateEntity();
        toUpdate.setId(project.getId());
        ProjectDTO updatedDto = mapper.toDto(toUpdate);

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

    @Test
    public void handleNoSuchProjectException() throws Exception {
        int id = project.getId();
        when(service.findById(id))
                .thenThrow(new NoSuchProjectException(String.format("There is no employee with id = %d", id)));

        mockMvc.perform(get("/company/project/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(dto)))
                .andExpect(
                        content().json(TestUtils.objectToJsonString(
                                new ErrorResponse(
                                        HttpStatus.CONFLICT,
                                        String.format("There is no employee with id = %d", id)
                                )
                        ))
                );
        verify(service, times(1)).findById(id);
    }

    @Test
    public void handleProjectAlreadyExistsException() throws Exception {
        when(service.save(project))
                .thenThrow(new ProjectAlreadyExistsException("Project already exists!"));

        mockMvc.perform(post("/company/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(dto)))
                .andExpect(
                        content().json(
                                TestUtils.objectToJsonString(
                                        new ErrorResponse(
                                                HttpStatus.CONFLICT,
                                                "Project already exists!"
                                        )
                                )
                        )
                );
        verify(service, times(1)).save(project);
    }
}
