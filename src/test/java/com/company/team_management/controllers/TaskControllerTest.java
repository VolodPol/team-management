package com.company.team_management.controllers;

import com.company.team_management.dto.TaskDTO;
import com.company.team_management.dto.mapper.impl.TaskMapper;
import com.company.team_management.entities.Project;
import com.company.team_management.entities.Task;
import com.company.team_management.security.config.SecurityConfig;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@ComponentScan(basePackages = "com.company.team_management.dto.mapper")
@Import(SecurityConfig.class)
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
        task.setProject(new Project());
        dto = mapper.toDto(task);
    }

    @Test
    public void fetchAllTasks() throws Exception {
        List<Task> tasks = List.of(task);
        List<TaskDTO> dtoList = mapper.collectionToDto(tasks);
        when(service.findAll()).thenReturn(tasks);

        mockMvc.perform(get("/company/tasks")
                        .header("X-API-KEY", "tm07To05ken*")
                        .contentType(MediaType.APPLICATION_JSON))
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
                        .header("X-API-KEY", "tm07To05ken*")
                        .contentType(MediaType.APPLICATION_JSON))
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
                        .header("X-API-KEY", "tm07To05ken*")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(task)))
                .andExpectAll(
                        content().json(TestUtils.objectToJsonString(dto)),
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION)
                );
        verify(service, times(1)).save(task);
    }

    @Test
    public void deleteExistingTaskById() throws Exception {
        doNothing().when(service).deleteById(task.getId());

        mockMvc.perform(delete("/company/task/{id}", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-KEY", "tm07To05ken*")
                )
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
        toUpdate.setProject(new Project());
        TaskDTO updatedDto = mapper.toDto(toUpdate);

        when(service.updateById(toUpdate.getId(), toUpdate))
                .thenReturn(toUpdate);

        mockMvc.perform(put("/company/task/{id}", task.getId())
                        .header("X-API-KEY", "tm07To05ken*")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJsonString(toUpdate)))
                .andExpectAll(
                        content().json(TestUtils.objectToJsonString(updatedDto)),
                        status().isOk()
                );
        verify(service, times(1)).updateById(toUpdate.getId(), toUpdate);
    }
}
