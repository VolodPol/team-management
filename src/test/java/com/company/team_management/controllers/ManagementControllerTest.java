package com.company.team_management.controllers;

import com.company.team_management.EmployeeProvider;
import com.company.team_management.ProjectProvider;
import com.company.team_management.TestEntityProvider;
import com.company.team_management.TestUtils;
import com.company.team_management.dto.EmployeeDTO;
import com.company.team_management.dto.EmployeeMapper;
import com.company.team_management.entities.Employee;
import com.company.team_management.entities.Project;
import com.company.team_management.services.ManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ManagementControllerTest {

    private MockMvc mvc;
    @Autowired
    @InjectMocks
    private ManagementController controller;
    @Mock
    private ManagementService service;
    @Mock
    private EmployeeMapper mapper;
    private Employee employee;
    private Project project;
    private final TestEntityProvider<Employee> empProvider;
    private final TestEntityProvider<Project> projectProvider;

    {
        empProvider = new EmployeeProvider();
        projectProvider = new ProjectProvider();
    }

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .defaultRequest(get("/**").accept(MediaType.APPLICATION_JSON))
                .alwaysExpect(content().contentType("application/json"))
                .build();
        employee = empProvider.generateEntity();
        project = projectProvider.generateEntity();
        employee.setId(TestUtils.generateId());
        project.setId(TestUtils.generateId());
    }

    @Test
    public void addNewEmpToExistingProjectById() throws Exception {
        Employee initCopy = shallowCopy(employee);
        employee.addProject(project);
        EmployeeDTO updated = empToDTO(employee);

        when(service.addNewEmpToProject(project.getId(), initCopy)).thenReturn(employee);
        when(mapper.toDTO(employee)).thenReturn(updated);

        mvc.perform(post("/company/manage/addProject/{id}", project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(initCopy)))
                .andExpect(content().json(TestUtils.objectToJsonString(updated)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        verify(service, times(1)).addNewEmpToProject(project.getId(), initCopy);
        verify(mapper, times(1)).toDTO(employee);
    }


    @Test
    public void addExistingEmployeeToProjectByIds() throws Exception {
        employee.addProject(project);
        EmployeeDTO updated = empToDTO(employee);

        when(service.addEmpByIdToProject(employee.getId(), project.getId()))
                .thenReturn(employee);
        when(mapper.toDTO(employee)).thenReturn(updated);

        mvc.perform(post("/company/manage/addProject?employee={empId}&project={projectId}",
                employee.getId(), project.getId()))
                .andExpectAll(
                        content().json(TestUtils.objectToJsonString(updated)),
                        content().contentType(MediaType.APPLICATION_JSON),
                        status().isOk()
                );
        verify(service, times(1)).addEmpByIdToProject(employee.getId(), project.getId());
        verify(mapper, times(1)).toDTO(employee);
    }

    @Test
    public void removeProjectFromEmployeeByIds() throws Exception {
        doNothing().when(service).removeProjectFromEmployee(employee.getId(), project.getId());
        String message = String.format("Project with id = %d was deleted from user with id = %d",
                project.getId(), employee.getId());

        mvc.perform(post("/company/manage/removeProject?employee={id}&project={id}",
                        employee.getId(), project.getId()))
                .andExpectAll(
                        content().string(message),
                        status().isOk()
                );
        verify(service, times(1))
                .removeProjectFromEmployee(employee.getId(), project.getId());
    }

    private Employee shallowCopy(Employee employee) {
        return new Employee.Builder()
                .addId(employee.getId())
                .addFullName(employee.getFullName())
                .addEmail(employee.getEmail())
                .addType(employee.getType())
                .addLevel(employee.getLevel())
                .addOccupation(employee.getOccupation())
                .build();
    }

    private EmployeeDTO empToDTO(Employee employee) {
        return new EmployeeDTO(
                employee.getId(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getOccupation().toString(),
                employee.getLevel().toString(),
                employee.getType().toString(),
                employee.getProjects().stream()
                        .map(Project::getTitle)
                        .toList()
        );
    }
}

