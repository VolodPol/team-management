package com.company.team_management.repositories;

import com.company.team_management.ProjectProvider;
import com.company.team_management.TestEntityProvider;
import com.company.team_management.entities.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProjectRepositoryTest {
    @Autowired
    private ProjectRepository repository;
    private Project project;
    private List<Project> projects;
    private final TestEntityProvider<Project> entityProvider = new ProjectProvider();

    @BeforeEach
    public void setUp() {
        repository.deleteAllInBatch();
        project = entityProvider.generateEntity();
        projects = entityProvider.generateEntityList();
    }

    @Test
    public void findAllProjects() {
        repository.saveAll(projects);
        List<Project> actual = repository.findAll();

        assertNotNull(actual);
        assertIterableEquals(projects, actual);
    }

    @Test
    public void findProjectById() {
        Project another = projects.get(0);
        repository.saveAll(projects);
        repository.save(project);

        assertEquals(project, repository.findById(project.getId()).orElse(null));
        assertNotEquals(project, repository.findById(another.getId()).orElse(null));
    }

    @Test
    public void saveProject() {
        repository.save(project);
        assertAll(
                () -> assertEquals(project, repository.findById(project.getId()).orElse(null)),
                () -> assertNull(repository.findById(project.getId() + 1).orElse(null))
        );
    }

    @Test
    public void deleteProjectById() {
        repository.saveAll(projects);

        int firstId = projects.get(0).getId();
        repository.deleteById(firstId);
        repository.deleteById(firstId + projects.get(1).getId());

        assertEquals(projects.size() - 1, repository.findAll().size());
    }

    @Test
    public void updateExistingProject() {
        repository.save(project);

        Project toUpdate = repository.findById(project.getId()).orElseThrow();
        toUpdate.setBudget(toUpdate.getBudget() + 1000);
        toUpdate.setFinished(!toUpdate.getFinished());
        repository.save(toUpdate);

        assertEquals(toUpdate, repository.findById(project.getId()).orElse(null));
    }
}