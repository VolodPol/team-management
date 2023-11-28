package com.company.team_management.services;

import com.company.team_management.utils.test_data_provider.ProgrammerProvider;
import com.company.team_management.utils.test_data_provider.ProjectProvider;
import com.company.team_management.utils.test_data_provider.TestEntityProvider;
import com.company.team_management.utils.TestUtils;
import com.company.team_management.entities.Programmer;
import com.company.team_management.entities.Project;
import com.company.team_management.services.impl.ProgrammerService;
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
    private ProgrammerService empService;
    @Mock
    private ProjectService projectService;
    private Programmer programmer;
    private Project project;
    private final TestEntityProvider<Programmer> employeeProvider;
    private final TestEntityProvider<Project> projectProvider;

    {
        employeeProvider = new ProgrammerProvider();
        projectProvider = new ProjectProvider();
    }

    @BeforeEach
    public void setUp() {
        programmer = employeeProvider.generateEntity();
        project = projectProvider.generateEntity();
        programmer.setId(TestUtils.generateId());
        project.setId(TestUtils.generateId());
    }

    @Test
    public void addNewEmployeeToExistingProject() {
        when(projectService.findById(project.getId())).thenReturn(project);
        when(empService.save(programmer)).thenReturn(programmer);
        Programmer found = service.addNewProgrammerToProject(project.getId(), programmer);

        assertEquals(programmer, found);
        assertTrue(found.getProjects().contains(project));
        verify(projectService, times(1)).findById(project.getId());
        verify(empService, times(1)).save(programmer);
    }

    @Test
    public void removeExistingEmpFromProject() {
        when(empService.findById(programmer.getId())).thenReturn(programmer);
        when(projectService.findById(project.getId())).thenReturn(project);

        service.removeProjectFromProgrammer(programmer.getId(), project.getId());
        assertFalse(programmer.getProjects().contains(project));
        verify(empService, times(1)).findById(programmer.getId());
        verify(projectService, times(1)).findById(project.getId());
    }
    @Test
    public void addExistingEmpToProject() {
        when(empService.findById(programmer.getId())).thenReturn(programmer);
        when(projectService.findById(project.getId())).thenReturn(project);

        Programmer added = service.addProgrammerByIdToProject(programmer.getId(), project.getId());
        assertTrue(
                added.getProjects().contains(project)
        );
        verify(empService, times(1)).findById(programmer.getId());
        verify(projectService, times(1)).findById(project.getId());
    }
}

