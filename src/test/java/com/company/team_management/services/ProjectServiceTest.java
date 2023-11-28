package com.company.team_management.services;

import com.company.team_management.utils.test_data_provider.ProjectProvider;
import com.company.team_management.utils.test_data_provider.TestEntityProvider;
import com.company.team_management.utils.TestUtils;
import com.company.team_management.entities.Project;
import com.company.team_management.exceptions.no_such.NoSuchEntityException;
import com.company.team_management.exceptions.already_exists.EntityExistsException;
import com.company.team_management.repositories.ProjectRepository;
import com.company.team_management.services.impl.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class ProjectServiceTest {
    @Mock
    private ProjectRepository repository;
    @Autowired
    @InjectMocks
    private ProjectService service;
    private Project project;
    private final TestEntityProvider<Project> entityProvider = new ProjectProvider();

    @BeforeEach
    public void setUp() {
        project = entityProvider.generateEntity();
    }

    @Test
    public void findAllProjects() {
        List<Project> projects = entityProvider.generateEntityList();
        when(repository.findAllFetch()).thenReturn(projects);

        List<Project> actual = service.findAll();
        assertIterableEquals(projects, actual);
        verify(repository, times(1)).findAllFetch();
    }

    @Test
    public void findProjectById() {
        int projectId = TestUtils.generateId();
        when(repository.findByIdFetch(projectId)).thenReturn(Optional.of(project));

        assertAll(
                () -> assertEquals(project, service.findById(projectId)),
                () -> assertThrows(NoSuchEntityException.class, () -> service.findById(projectId + 1))
        );
        verify(repository, times(1)).findByIdFetch(projectId);
    }


    @Test
    public void saveExistingProjectThrowsException() {
        project.setId(TestUtils.generateId());
        when(repository.findById(project.getId())).thenReturn(Optional.of(project));

        assertThrowsExactly(EntityExistsException.class, () -> service.save(project),
                "Project already exists!");
        verify(repository, times(1)).findById(any());
    }

    @Test
    public void saveProjectWithoutId() {
        when(repository.save(project)).thenReturn(project);

        assertEquals(project, service.save(project));
        verify(repository, times(1)).save(project);
        verify(repository, times(0)).findById(any());
    }

    @Test
    public void saveProjectWithSpecifiedId() {
        project.setId(TestUtils.generateId());
        when(repository.save(project)).thenReturn(project);
        when(repository.findById(project.getId())).thenReturn(Optional.empty());

        assertEquals(project, service.save(project));
        verify(repository, times(1)).findById(any());
        verify(repository, times(1)).save(project);
    }

    @Test
    public void deleteExistingProjectById() {
        project.setId(TestUtils.generateId());
        when(repository.findById(any())).thenReturn(Optional.of(project));
        doNothing().when(repository).deleteById(project.getId());

        service.deleteById(project.getId());
        verify(repository, times(1)).deleteById(any());
    }

    @Test
    public void deleteNonExistingProjectById() {
        int id = TestUtils.generateId();
        project.setId(id);
        when(repository.findById(id))
                .thenThrow(new NoSuchEntityException(String.format("There is no project with id = %d", id)));

        assertThrowsExactly(NoSuchEntityException.class, () -> service.deleteById(id),
                String.format("There is no project with id = %d", id));
        verify(repository, times(1)).findById(id);
    }

    @Test
    public void updateExistingProject() {
        int projectId = TestUtils.generateId();
        project.setId(projectId);
        when(repository.findByIdFetch(projectId)).thenReturn(Optional.of(project));

        Project updated = entityProvider.generateEntity();
        updated.setId(projectId);
        updated.setBudget(updated.getBudget() + 1000);

        assertEquals(updated, service.updateById(projectId, updated));
        verify(repository, times(1)).findByIdFetch(any());
    }

    @Test
    public void updateNonExistingProject() {
        project.setId(TestUtils.generateId());
        when(repository.findByIdFetch(project.getId()))
                .thenThrow(new EntityExistsException("Project already exists!"));

        assertThrowsExactly(EntityExistsException.class, () -> service.updateById(project.getId(), project),
                "Project already exists!");
        verify(repository, times(0)).save(project);
    }
}