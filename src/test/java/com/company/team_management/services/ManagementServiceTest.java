package com.company.team_management.services;

import com.company.team_management.EmployeeProvider;
import com.company.team_management.ProjectProvider;
import com.company.team_management.TestEntityProvider;
import com.company.team_management.TestUtils;
import com.company.team_management.entities.Employee;
import com.company.team_management.entities.Project;
import com.company.team_management.repositories.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
    private final TestEntityProvider<Employee> employeeProvider;
    private final TestEntityProvider<Project> projectProvider;

    {
        employeeProvider = new EmployeeProvider();
        projectProvider = new ProjectProvider();
    }

    @Test
    public void addNewEmployeeToExistingProject() {
        Employee employee = employeeProvider.generateEntity();
        employee.setId(TestUtils.generateId());
        Project project = projectProvider.generateEntity();
        project.setId(TestUtils.generateId());

        when(projectService.findById(project.getId())).thenReturn(project);

        assertEquals(employee, service.addEmpToProject(project.getId(), employee));
    }
}

