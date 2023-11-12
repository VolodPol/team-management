package com.company.team_management.repositories;

import com.company.team_management.entities.Project;
import com.company.team_management.entities.Task;
import com.company.team_management.utils.test_data_provider.ProjectProvider;
import com.company.team_management.utils.test_data_provider.TaskProvider;
import com.company.team_management.utils.test_data_provider.TestEntityProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TaskRepositoryTest {
    private final TaskRepository taskRepo;
    private Task task;
    private List<Task> tasks;
    private final Project project;
    private final TestEntityProvider<Task> taskProvider = new TaskProvider();

    @Autowired
    public TaskRepositoryTest(TaskRepository taskRepo, ProjectRepository projectRepo) {
        this.taskRepo = taskRepo;
        project = new ProjectProvider().generateEntity();
        projectRepo.save(project);
    }

    @BeforeEach
    public void setUp() {
        taskRepo.deleteAllInBatch();
        task = taskProvider.generateEntity();
        tasks = taskProvider.generateEntityList();

        task.setProject(project);
        tasks.forEach(task -> task.setProject(project));
    }

    @Test
    public void findAllTasks() {
        taskRepo.saveAll(tasks);

        List<Task> actual = taskRepo.findAll();

        assertNotNull(actual);
        assertIterableEquals(tasks, actual);
    }

    @Test
    public void findTaskById() {
        Task another = tasks.get(0);
        taskRepo.saveAll(tasks);
        taskRepo.save(task);

        assertEquals(task, taskRepo.findById(task.getId()).orElse(null));
        assertNotEquals(task, taskRepo.findById(another.getId()).orElse(null));
    }

    @Test
    public void saveTask() {
        taskRepo.save(task);

        assertAll(
                () -> assertEquals(task, taskRepo.findById(task.getId()).orElse(null)),
                () -> assertNull(taskRepo.findById(task.getId() + 1).orElse(null))
        );
    }

    @Test
    public void deleteTaskById() {
        taskRepo.saveAll(tasks);

        int firstId = tasks.get(0).getId();
        taskRepo.deleteById(firstId);
        taskRepo.deleteById(firstId + tasks.get(1).getId());

        assertEquals(tasks.size() - 1, taskRepo.findAll().size());
    }

    @Test
    public void updateExistingTask() {
        taskRepo.save(task);

        Task toUpdate = taskRepo.findById(task.getId()).orElseThrow();
        toUpdate.setName("new name");
        taskRepo.save(toUpdate);

        assertEquals(toUpdate, taskRepo.findById(task.getId()).orElse(null));
    }
}
