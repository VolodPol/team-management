package com.company.team_management.repositories;

import com.company.team_management.entities.Project;
import com.company.team_management.entities.Task;
import com.company.team_management.utils.test_data_provider.ProgrammerProvider;
import com.company.team_management.utils.test_data_provider.TestEntityProvider;
import com.company.team_management.entities.Programmer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ProgrammerRepositoryTest {

    @Autowired
    private ProgrammerRepository repository;
    private Programmer programmer;
    private final TestEntityProvider<Programmer> entityProvider = new ProgrammerProvider();

    @BeforeEach
    public void setUp() {
        repository.deleteAllInBatch();
        programmer = entityProvider.generateEntity();
    }

    @AfterEach
    public void tearDown() {
        repository.deleteAll();
        programmer = null;
    }

    @Test
    public void saveProductCase() {
        Programmer found = repository
                .findById(extractIdOfSavedProgrammer())
                .orElse(null);

        assertEquals(programmer, found);
    }

    @Test
    public void getAllCase() {
        List<Programmer> programmerList = entityProvider.generateEntityList();
        repository.saveAll(programmerList);

        List<Programmer> fetched = repository.findAll();
        assertAll(
                () -> assertEquals(programmerList.get(0).getEmail(), fetched.get(0).getEmail()),
                () -> assertEquals(programmerList.get(1).getFullName(), fetched.get(1).getFullName())
        );
    }

    @Test
    public void getByIdCase() {
        int id = extractIdOfSavedProgrammer();
        Programmer actual = repository.findById(id)
                .orElse(null);

        assertEquals(programmer, actual);
    }

    @Test
    public void updateExistingEmployeeCase() {
        int id = extractIdOfSavedProgrammer();
        String newName = "jeremy";
        Programmer.Level newLevel = Programmer.Level.MIDDLE;

        programmer.setFullName(newName);
        programmer.setLevel(newLevel);
        repository.save(programmer);

        Programmer changed = repository.findById(id).orElse(new Programmer());
        assertEquals(newName, changed.getFullName());
        assertEquals(newLevel, changed.getLevel());
    }

    @Test
    public void deleteEmployeeCase() {
        repository.deleteById(extractIdOfSavedProgrammer());

        assertEquals(0, repository.findAll().size());
    }

    @Test
    public void testFetchWithTasks() {
        Project project = new Project.Builder()
                .addTitle("test title")
                .addGoal("test goal")
                .addBudget(10000L)
                .build();

        Task task1 = new Task();
        task1.setName("task name 1");
        task1.setStatus(Task.Status.ACTIVE);
        project.addTask(task1);

        Task task2 = new Task();
        task2.setName("task name 2");
        task2.setStatus(Task.Status.FINISHED);
        project.addTask(task2);

        programmer.addProject(project);
        repository.save(programmer);
        List<Programmer> programmers = repository.findAllFetchTask();
        Programmer fetchedProgrammer = programmers.get(0);
        List<Project> fetchedProjects = fetchedProgrammer.getProjects().stream().toList();
        Project fetchedProject = fetchedProjects.get(0);
        Set<Task> fetchedTasks = fetchedProject.getTasks();

        assertEquals(programmer, fetchedProgrammer);
        assertIterableEquals(List.of(project), fetchedProjects);
        assertTrue(fetchedTasks.stream().allMatch(task -> task.equals(task1) || task.equals(task2)));
    }

    private int extractIdOfSavedProgrammer() {
        repository.save(programmer);
        return programmer.getId();
    }
}