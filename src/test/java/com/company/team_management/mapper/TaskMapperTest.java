package com.company.team_management.mapper;


import com.company.team_management.dto.TaskDTO;
import com.company.team_management.entities.Project;
import com.company.team_management.entities.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TaskMapperImpl.class)
public class TaskMapperTest {
    @Autowired
    private TaskMapper mapper;

    @Test
    public void whenMapsFromEntityToDto_ThenCorrect() {
        Task task = Task.builder()
                .id(1)
                .name("task")
                .status(Task.Status.ACTIVE)
                .project(new Project.Builder()
                        .addTitle("project 1")
                        .build())
                .build();

        TaskDTO dto = mapper.entityToDTO(task);
        assertNotNull(dto);
        assertAll(
                () -> assertEquals(dto.getId(), task.getId()),
                () -> assertEquals(dto.getName(), task.getName()),
                () -> assertEquals(dto.getStatus(), task.getStatus().toString()),
                () -> assertEquals(dto.getProject(), task.getProject().getTitle())
        );
    }

    @Test
    public void whenMapsDtoToEntity_ThenCorrect() {
        TaskDTO dto = new TaskDTO(
                1,
                "task 1",
                "ACTIVE",
                "project"
        );
        Task programmer = mapper.dtoToEntity(dto);

        assertNotNull(programmer);
        assertAll(
                () -> assertEquals(dto.getId(), programmer.getId()),
                () -> assertEquals(dto.getName(), programmer.getName()),
                () -> assertEquals(dto.getStatus(), programmer.getStatus().toString()),
                () -> assertNull(programmer.getProject())
        );
    }
}
