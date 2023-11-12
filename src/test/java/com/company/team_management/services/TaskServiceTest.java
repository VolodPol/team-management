package com.company.team_management.services;

import com.company.team_management.entities.Task;
import com.company.team_management.exceptions.already_exists.TaskAlreadyExistsException;
import com.company.team_management.exceptions.no_such.NoSuchTaskException;
import com.company.team_management.repositories.TaskRepository;
import com.company.team_management.services.impl.TaskService;
import com.company.team_management.utils.TestUtils;
import com.company.team_management.utils.test_data_provider.TaskProvider;
import com.company.team_management.utils.test_data_provider.TestEntityProvider;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class TaskServiceTest {
    @Mock
    private TaskRepository repository;
    @Autowired
    @InjectMocks
    private TaskService service;
    private Task task;
    private final TestEntityProvider<Task> entityProvider = new TaskProvider();

    @BeforeEach
    public void setUp() {
        task = entityProvider.generateEntity();
    }

    @Test
    public void findAllTasks() {
        List<Task> tasks = entityProvider.generateEntityList();
        when(repository.findAll()).thenReturn(tasks);

        List<Task> actual = service.findAll();
        assertIterableEquals(tasks, actual);
        verify(repository, times(1)).findAll();
    }

    @Test
    public void findTaskById() {
        int taskId = TestUtils.generateId();
        when(repository.findById(taskId)).thenReturn(Optional.of(task));

        assertAll(
                () -> assertEquals(task, service.findById(taskId)),
                () -> assertThrows(NoSuchTaskException.class, () -> service.findById(taskId + 1))
        );
        verify(repository, times(1)).findById(taskId);
    }


    @Test
    public void saveExistingTaskThrowsException() {
        task.setId(TestUtils.generateId());
        when(repository.findById(task.getId())).thenReturn(Optional.of(task));

        assertThrowsExactly(TaskAlreadyExistsException.class, () -> service.save(task),
                "Task already exists!");
        verify(repository, times(1)).findById(any());
    }

    @Test
    public void saveTaskWithoutId() {
        when(repository.save(task)).thenReturn(task);

        assertEquals(task, service.save(task));
        verify(repository, times(1)).save(task);
        verify(repository, times(0)).findById(any());
    }

    @Test
    public void saveTaskWithSpecifiedId() {
        task.setId(TestUtils.generateId());
        when(repository.save(task)).thenReturn(task);
        when(repository.findById(task.getId())).thenReturn(Optional.empty());

        assertEquals(task, service.save(task));
        verify(repository, times(1)).findById(any());
        verify(repository, times(1)).save(task);
    }

    @Test
    public void deleteExistingTaskById() {
        task.setId(TestUtils.generateId());
        when(repository.findById(any())).thenReturn(Optional.of(task));
        doNothing().when(repository).deleteById(task.getId());

        service.deleteById(task.getId());
        verify(repository, times(1)).deleteById(any());
    }

    @Test
    public void deleteNonExistingTaskById() {
        int id = TestUtils.generateId();
        task.setId(id);
        when(repository.findById(id))
                .thenThrow(new NoSuchTaskException(String.format("There is no task with id = %d", id)));

        assertThrowsExactly(NoSuchTaskException.class, () -> service.deleteById(id),
                String.format("There is no task with id = %d", id));
        verify(repository, times(1)).findById(id);
    }

    @Test
    public void updateExistingTask() {
        int taskId = TestUtils.generateId();
        task.setId(taskId);
        when(repository.findById(taskId)).thenReturn(Optional.of(task));

        Task updated = entityProvider.generateEntity();
        updated.setId(taskId);
        updated.setName("updated");

        assertEquals(updated, service.updateById(taskId, updated));
        verify(repository, times(1)).findById(any());
    }

    @Test
    public void updateNonExistingTask() {
        task.setId(TestUtils.generateId());
        when(repository.findById(task.getId()))
                .thenThrow(new TaskAlreadyExistsException("Task already exists!"));

        assertThrowsExactly(TaskAlreadyExistsException.class, () -> service.updateById(task.getId(), task),
                "Task already exists!");
        verify(repository, times(0)).save(task);
    }
}

