package com.company.team_management.services;

import com.company.team_management.EmployeeProvider;
import com.company.team_management.ProjectProvider;
import com.company.team_management.TestEntityProvider;
import com.company.team_management.TestUtils;
import com.company.team_management.entities.Employee;
import com.company.team_management.entities.Project;
import com.company.team_management.services.impl.EmployeeService;
import com.company.team_management.services.impl.ManagementService;
import com.company.team_management.services.impl.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class ManagementServiceTest {
    @Autowired
    @InjectMocks
    private ManagementService service;
    @Mock
    private EmployeeService empService;
    @Mock
    private ProjectService projectService;
    private Employee employee;
    private Project project;
    private final TestEntityProvider<Employee> employeeProvider;
    private final TestEntityProvider<Project> projectProvider;

    {
        employeeProvider = new EmployeeProvider();
        projectProvider = new ProjectProvider();
    }

    @BeforeEach
    public void setUp() {
        employee = employeeProvider.generateEntity();
        project = projectProvider.generateEntity();
        employee.setId(TestUtils.generateId());
        project.setId(TestUtils.generateId());
    }

    @Test
    public void addNewEmployeeToExistingProject() {
        when(projectService.findById(project.getId())).thenReturn(project);
        when(empService.save(employee)).thenReturn(employee);
        Employee found = service.addNewEmpToProject(project.getId(), employee);

        assertEquals(employee, found);
        assertTrue(found.getProjects().contains(project));
        verify(projectService, times(1)).findById(project.getId());
        verify(empService, times(1)).save(employee);
    }

    @Test
    public void removeExistingEmpFromProject() {
        when(empService.findById(employee.getId())).thenReturn(employee);
        when(projectService.findById(project.getId())).thenReturn(project);

        service.removeProjectFromEmployee(employee.getId(), project.getId());
        assertFalse(employee.getProjects().contains(project));
        verify(empService, times(1)).findById(employee.getId());
        verify(projectService, times(1)).findById(project.getId());
    }
    @Test
    public void addExistingEmpToProject() {
        when(empService.findById(employee.getId())).thenReturn(employee);
        when(projectService.findById(project.getId())).thenReturn(project);

        Employee added = service.addEmpByIdToProject(employee.getId(), project.getId());
        assertTrue(
                added.getProjects().contains(project)
        );
        verify(empService, times(1)).findById(employee.getId());
        verify(projectService, times(1)).findById(project.getId());
    }
}

