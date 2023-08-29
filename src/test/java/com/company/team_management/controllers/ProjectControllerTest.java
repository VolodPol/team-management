package com.company.team_management.controllers;

import com.company.team_management.ProjectProvider;
import com.company.team_management.TestEntityProvider;
import com.company.team_management.TestUtils;
import com.company.team_management.entities.Project;
import com.company.team_management.exceptions.ErrorResponse;
import com.company.team_management.exceptions.project.NoSuchProjectException;
import com.company.team_management.exceptions.project.ProjectAlreadyExistsException;
import com.company.team_management.services.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {
    @Mock
    private ProjectService service;
    @InjectMocks
    private ProjectController controller;
    private MockMvc mockMvc;
    private Project project;
    private final TestEntityProvider<Project> entityProvider = new ProjectProvider();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .defaultRequest(get("/**").accept(MediaType.APPLICATION_JSON))
                .alwaysExpect(content().contentType(MediaType.APPLICATION_JSON))
                .build();
        project = entityProvider.generateEntity();
    }

    @Test
    public void fetchAllProjects() throws Exception {
        List<Project> projects = entityProvider.generateEntityList();
        when(service.findAll()).thenReturn(projects);

        mockMvc.perform(get("/company/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJsonString(projects)))
                .andExpectAll(
                        status().isOk(),
                        content().json(TestUtils.objectToJsonString(projects))
                        );
        verify(service, times(1)).findAll();
    }

    @Test
    public void findProjectById() throws Exception {
        int projectId = TestUtils.generateId();
        project.setId(projectId);
        when(service.findById(projectId)).thenReturn(project);

        mockMvc.perform(get("/company/project/{id}", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJsonString(project)))
                .andExpectAll(
                        status().isFound(),
                        content().json(TestUtils.objectToJsonString(project))
                );
        verify(service, times(1)).findById(projectId);
    }

    @Test
    public void addNewProject() throws Exception {
        when(service.save(project)).thenReturn(project);

        mockMvc.perform(post("/company/project")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJsonString(project)))
                .andExpectAll(
                        content().json(TestUtils.objectToJsonString(project)),
                        status().isCreated()
                );
        verify(service, times(1)).save(project);
    }

    @Test
    public void deleteExistingProjectById() throws Exception {
        project.setId(TestUtils.generateId());
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
        project.setId(TestUtils.generateId());
        Project toUpdate = entityProvider.generateEntity();
        toUpdate.setId(project.getId());
        toUpdate.setFinished(!toUpdate.getFinished());

        when(service.updateById(toUpdate.getId(), toUpdate))
                .thenReturn(toUpdate);

        mockMvc.perform(put("/company/project/{id}", project.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJsonString(toUpdate)))
                .andExpectAll(
                        content().json(TestUtils.objectToJsonString(toUpdate)),
                        status().isOk()
                );
        verify(service, times(1)).updateById(toUpdate.getId(), toUpdate);
    }

    @Test
    public void handleNoSuchProjectException() throws Exception {
        int id = TestUtils.generateId();
        when(service.findById(id))
                .thenThrow(new NoSuchProjectException(String.format("There is no employee with id = %d", id)));

        mockMvc.perform(get("/company/project/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJsonString(project)))
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
                .content(TestUtils.objectToJsonString(project)))
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
