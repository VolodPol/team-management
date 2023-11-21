package com.company.team_management.repositories;

import com.company.team_management.entities.Programmer;
import com.company.team_management.entities.Project;
import com.company.team_management.entities.Task;
import com.company.team_management.utils.test_data_provider.ProjectProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomProjectRepositoryImplTest {
    @Autowired
    private CustomProjectRepositoryImpl repository;
    @Autowired
    private ProjectRepository helperRepository;
    private List<Project> projects;
    private final ProjectProvider provider = new ProjectProvider();

    @BeforeEach
    public void setUp() {
        helperRepository.deleteAllInBatch();
        projects = provider.generateEntityList();
    }

    @Test
    public void testFetchProjectsWithProgrammersAndTasks() {
        persistEntities();

        long minBudget = projects.stream()
                .mapToLong(Project::getBudget)
                .min()
                .orElse(0L);
        long maxBudget = projects.stream()
                .mapToLong(Project::getBudget)
                .reduce(0L, Math::max);

        assertIterableEquals(projects, repository.getAllFetchAllWithinBudget(minBudget, maxBudget));
        assertEquals(0, repository.getAllFetchAllWithinBudget(maxBudget, minBudget).size());
        assertEquals(1, repository.getAllFetchAllWithinBudget(maxBudget - 1, maxBudget).size());
    }

    private void persistEntities() {
        for (Project project : projects) {
            Task task = new Task();
            task.setName("task name");
            task.setStatus(Task.Status.FINISHED);

            project.addTask(task);
            Programmer programmer = new Programmer.Builder()
                    .addFullName("fullName")
                    .addEmail("email@gmail.com")
                    .addType(Programmer.Type.DEVELOPER)
                    .addLevel(Programmer.Level.JUNIOR)
                    .build();
            programmer.addProject(project);
        }
        helperRepository.saveAll(projects);
    }
}