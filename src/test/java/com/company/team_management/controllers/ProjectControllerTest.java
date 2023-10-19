package com.company.team_management.controllers;

import com.company.team_management.ProjectProvider;
import com.company.team_management.TestEntityProvider;
import com.company.team_management.TestUtils;
import com.company.team_management.dto.ProjectDTO;
import com.company.team_management.dto.ProjectMapper;
import com.company.team_management.entities.Employee;
import com.company.team_management.entities.Project;
import com.company.team_management.exceptions.ErrorResponse;
import com.company.team_management.exceptions.project.NoSuchProjectException;
import com.company.team_management.exceptions.project.ProjectAlreadyExistsException;
import com.company.team_management.services.impl.ProjectService;
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
    @Mock
    private ProjectMapper mapper;
    @InjectMocks
    private ProjectController controller;
    private MockMvc mockMvc;
    private Project project;
    private ProjectDTO dto;
    private final TestEntityProvider<Project> entityProvider = new ProjectProvider();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .defaultRequest(get("/**").accept(MediaType.APPLICATION_JSON))
                .alwaysExpect(content().contentType(MediaType.APPLICATION_JSON))
                .build();
        project = entityProvider.generateEntity();
        project.setId(TestUtils.generateId());
        dto = projectToDTO(project);
    }

    @Test
    public void fetchAllProjects() throws Exception {
        List<Project> projects = List.of(project);
        List<ProjectDTO> dtoList = projectsToDTOList(projects);
        when(service.findAll()).thenReturn(projects);
        when(mapper.toDTO(project)).thenReturn(dto);

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
        when(mapper.toDTO(project)).thenReturn(dto);

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
        when(mapper.toDTO(project)).thenReturn(dto);

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
        toUpdate.setFinished(!toUpdate.getFinished());
        ProjectDTO updatedDto = projectToDTO(toUpdate);

        when(service.updateById(toUpdate.getId(), toUpdate))
                .thenReturn(toUpdate);
        when(mapper.toDTO(toUpdate)).thenReturn(updatedDto);

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

    private ProjectDTO projectToDTO(Project project) {
        return new ProjectDTO(
                project.getId(),
                project.getTitle(),
                project.getGoal(),
                String.valueOf(project.getBudget()),
                project.getFinished(),
                project.getEmployees().stream()
                        .map(Employee::getFullName)
                        .toList()
                );
    }

    private List<ProjectDTO> projectsToDTOList(List<Project> projects) {
        return projects.stream()
                .map(this::projectToDTO)
                .toList();
    }
}
