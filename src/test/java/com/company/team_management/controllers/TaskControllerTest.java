package com.company.team_management.controllers;

import com.company.team_management.dto.TaskDTO;
import com.company.team_management.dto.mapper.impl.TaskMapper;
import com.company.team_management.entities.Task;
import com.company.team_management.exceptions.ErrorResponse;
import com.company.team_management.exceptions.already_exists.TaskAlreadyExistsException;
import com.company.team_management.exceptions.no_such.NoSuchTaskException;
import com.company.team_management.services.impl.TaskService;
import com.company.team_management.utils.TestUtils;
import com.company.team_management.utils.test_data_provider.TaskProvider;
import com.company.team_management.utils.test_data_provider.TestEntityProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@ComponentScan(basePackages = "com.company.team_management.dto.mapper")
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TaskService service;
    @Autowired
    private TaskMapper mapper;
    private Task task;
    private TaskDTO dto;
    private final TestEntityProvider<Task> entityProvider = new TaskProvider();

    @BeforeEach
    public void setUp() {
        task = entityProvider.generateEntity();
        task.setId(TestUtils.generateId());
        dto = mapper.toDto(task);
    }

    @Test
    public void fetchAllTasks() throws Exception {
        List<Task> tasks = List.of(task);
        List<TaskDTO> dtoList = mapper.collectionToDto(tasks);
        when(service.findAll()).thenReturn(tasks);

        mockMvc.perform(get("/company/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(dtoList)))
                .andExpectAll(
                        status().isOk(),
                        content().json(TestUtils.objectToJsonString(dtoList))
                );
        verify(service, times(1)).findAll();
    }

    @Test
    public void findTaskById() throws Exception {
        when(service.findById(task.getId())).thenReturn(task);

        mockMvc.perform(get("/company/task/{id}", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(dto)))
                .andExpectAll(
                        status().isFound(),
                        content().json(TestUtils.objectToJsonString(dto))
                );
        verify(service, times(1)).findById(task.getId());
    }

    @Test
    public void addNewTask() throws Exception {
        when(service.save(task)).thenReturn(task);

        mockMvc.perform(post("/company/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(dto)))
                .andExpectAll(
                        content().json(TestUtils.objectToJsonString(dto)),
                        status().isCreated()
                );
        verify(service, times(1)).save(task);
    }

    @Test
    public void deleteExistingTaskById() throws Exception {
        doNothing().when(service).deleteById(task.getId());

        mockMvc.perform(delete("/company/task/{id}", task.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        content().string("Successfully deleted!"),
                        status().isNoContent()
                );
        verify(service, times(1)).deleteById(task.getId());
    }

    @Test
    public void updateExistingTask() throws Exception {
        Task toUpdate = entityProvider.generateEntity();
        toUpdate.setId(task.getId());
        TaskDTO updatedDto = mapper.toDto(toUpdate);

        when(service.updateById(toUpdate.getId(), toUpdate))
                .thenReturn(toUpdate);

        mockMvc.perform(put("/company/task/{id}", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(updatedDto)))
                .andExpectAll(
                        content().json(TestUtils.objectToJsonString(updatedDto)),
                        status().isOk()
                );
        verify(service, times(1)).updateById(toUpdate.getId(), toUpdate);
    }

    @Test
    public void handleNoSuchTaskException() throws Exception {
        int id = task.getId();
        when(service.findById(id))
                .thenThrow(new NoSuchTaskException(String.format("There is no task with id = %d", id)));

        mockMvc.perform(get("/company/task/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(dto)))
                .andExpect(
                        content().json(TestUtils.objectToJsonString(
                                new ErrorResponse(
                                        HttpStatus.CONFLICT,
                                        String.format("There is no task with id = %d", id)
                                )
                        ))
                );
        verify(service, times(1)).findById(id);
    }

    @Test
    public void handleTaskAlreadyExistsException() throws Exception {
        when(service.save(task))
                .thenThrow(new TaskAlreadyExistsException("Task already exists!"));

        mockMvc.perform(post("/company/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(dto)))
                .andExpect(
                        content().json(
                                TestUtils.objectToJsonString(
                                        new ErrorResponse(
                                                HttpStatus.CONFLICT,
                                                "Task already exists!"
                                        )
                                )
                        )
                );
        verify(service, times(1)).save(task);
    }
}