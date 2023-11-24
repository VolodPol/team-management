package com.company.team_management.controllers;

import com.company.team_management.utils.test_data_provider.ProgrammerProvider;
import com.company.team_management.utils.test_data_provider.ProjectProvider;
import com.company.team_management.utils.test_data_provider.TestEntityProvider;
import com.company.team_management.utils.TestUtils;
import com.company.team_management.dto.ProgrammerDto;
import com.company.team_management.dto.mapper.impl.ProgrammerMapper;
import com.company.team_management.entities.Programmer;
import com.company.team_management.entities.Project;
import com.company.team_management.services.impl.ManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ManagementController.class)
@ComponentScan(basePackages = "com.company.team_management.dto.mapper")
public class ManagementControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ProgrammerMapper mapper;
    @MockBean
    private ManagementService service;
    private Programmer programmer;
    private Project project;
    private final TestEntityProvider<Programmer> programmerProvider;
    private final TestEntityProvider<Project> projectProvider;

    {
        programmerProvider = new ProgrammerProvider();
        projectProvider = new ProjectProvider();
    }

    @BeforeEach
    public void setUp() {
        programmer = programmerProvider.generateEntity();
        project = projectProvider.generateEntity();
        programmer.setId(TestUtils.generateId());
        project.setId(TestUtils.generateId());
    }

    @Test
    public void addNewEmpToExistingProjectById() throws Exception {
        Programmer initCopy = programmerProvider.generateEntity();
        programmer.addProject(project);
        ProgrammerDto updatedDto = mapper.toDto(programmer);

        when(service.addNewProgrammerToProject(project.getId(), initCopy)).thenReturn(programmer);

        mvc.perform(post("/company/manage/addProject/{id}", project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(initCopy)))
                .andExpectAll(
                        content().json(TestUtils.objectToJsonString(updatedDto)),
                        content().contentType(MediaType.APPLICATION_JSON),
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION)
                );
        verify(service, times(1)).addNewProgrammerToProject(project.getId(), initCopy);
    }


    @Test
    public void addExistingProgrammerToProjectByIds() throws Exception {
        programmer.addProject(project);
        ProgrammerDto updated = mapper.toDto(programmer);
        when(service.addProgrammerByIdToProject(programmer.getId(), project.getId()))
                .thenReturn(programmer);

        mvc.perform(post("/company/manage/addProject?programmer={empId}&project={projectId}",
                        programmer.getId(), project.getId()))
                .andExpectAll(
                        content().json(TestUtils.objectToJsonString(updated)),
                        content().contentType(MediaType.APPLICATION_JSON),
                        status().isOk()
                );
        verify(service, times(1)).addProgrammerByIdToProject(programmer.getId(), project.getId());
    }

    @Test
    public void removeProjectFromProgrammerByIds() throws Exception {
        doNothing().when(service).removeProjectFromProgrammer(programmer.getId(), project.getId());
        String message = String.format("Project with id = %d was deleted from user with id = %d",
                project.getId(), programmer.getId());

        mvc.perform(post("/company/manage/removeProject?programmer={id}&project={id}",
                        programmer.getId(), project.getId()))
                .andExpectAll(
                        content().string(message),
                        status().isOk()
                );
        verify(service, times(1))
                .removeProjectFromProgrammer(programmer.getId(), project.getId());
    }
}

