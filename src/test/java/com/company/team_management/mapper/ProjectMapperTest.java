package com.company.team_management.mapper;

import com.company.team_management.dto.ProjectDTO;
import com.company.team_management.dto.TaskDTO;
import com.company.team_management.entities.Programmer;
import com.company.team_management.entities.Project;
import com.company.team_management.entities.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ProjectMapperImpl.class, TaskMapperImpl.class})
public class ProjectMapperTest {
    @Autowired
    private ProjectMapper mapper;

    @Test
    public void whenMapsFromEntityToDto_ThenCorrect() {
        Project project = new Project.Builder()
                .addId(1)
                .addTitle("project 1")
                .addGoal("Project 1 goal.")
                .addBudget(20_000L)
                .build();
        project.addTask(Task.builder()
                .name("task 1")
                .status(Task.Status.ACTIVE)
                .project(project)
                .build());
        project.setProgrammers(Set.of(
                new Programmer.Builder()
                        .addFullName("programmer 1")
                        .build(),
                new Programmer.Builder()
                        .addFullName("programmer 2")
                        .build()
        ));

        ProjectDTO dto = mapper.entityToDTO(project);
        assertNotNull(dto);
        assertAll(
                () -> assertEquals(dto.getId(), project.getId()),
                () -> assertEquals(dto.getTitle(), project.getTitle()),
                () -> assertEquals(dto.getGoal(), project.getGoal()),
                () -> assertEquals(dto.getBudget(), String.valueOf(project.getBudget()))
        );
        assertAll(
                () -> assertEquals(dto.getTasks().size(), project.getTasks().size()),
                () -> assertIterableEquals(dto.getProgrammers(), project.getProgrammers().stream()
                        .map(Programmer::getFullName).toList())
        );
    }

    @Test
    public void whenMapsDtoToEntity_ThenCorrect() {
        List<TaskDTO> tasks = List.of(
                new TaskDTO(
                        1, "task 1", "ACTIVE", "project 1"
                ),
                new TaskDTO(
                        2, "task 2", "FINISHED", "project 2"
                )
        );
        ProjectDTO dto = new ProjectDTO(
                1,
                "project 1",
                "Goal 1.",
                "20000",
                List.of("Mike", "Phil", "Richard"),
                tasks
        );
        Project project = mapper.dtoToEntity(dto);

        assertNotNull(project);
        assertAll(
                () -> assertEquals(dto.getId(), project.getId()),
                () -> assertEquals(dto.getTitle(), project.getTitle()),
                () -> assertEquals(dto.getGoal(), project.getGoal()),
                () -> assertEquals(dto.getBudget(), String.valueOf(project.getBudget()))
        );
        assertTrue(project.getProgrammers().isEmpty());
        assertEquals(0, project.getTasks().size());
    }
}
