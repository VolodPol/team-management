package com.company.team_management.mapper;


import com.company.team_management.dto.ProgrammerDto;
import com.company.team_management.entities.Department;
import com.company.team_management.entities.Programmer;
import com.company.team_management.entities.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ProgrammerMapperImpl.class)
public class ProgrammerMapperTest {
    @Autowired
    private ProgrammerMapper mapper;

    @Test
    public void whenMapsFromEntityToDto_ThenCorrect() {
        Programmer programmer = new Programmer.Builder()
                .addId(1)
                .addFullName("programmer 1")
                .addEmail("pg@gmail.com")
                .addType(Programmer.Type.DEVOPS)
                .addLevel(Programmer.Level.MIDDLE)
                .build();
        programmer.setDepartment(Department.builder().name("department 1").build());
        programmer.addProject(new Project.Builder()
                .addTitle("project1").build());
        programmer.addProject(new Project.Builder()
                .addTitle("project2").build());

        ProgrammerDto dto = mapper.entityToDTO(programmer);
        assertNotNull(dto);
        assertAll(
                () -> assertEquals(dto.getId(), programmer.getId()),
                () -> assertEquals(dto.getName(), programmer.getFullName()),
                () -> assertEquals(dto.getEmail(), programmer.getEmail()),
                () -> assertEquals(dto.getDepartment(), programmer.getDepartment().getName()),
                () -> assertEquals(dto.getType(), programmer.getType().toString()),
                () -> assertEquals(dto.getLevel(), programmer.getLevel().toString())
        );
        assertIterableEquals(dto.getProjects(), programmer.getProjects().stream()
                .map(Project::getTitle).toList());
    }

    @Test
    public void whenMapsDtoToEntity_ThenCorrect() {
        ProgrammerDto dto = new ProgrammerDto(
                1,
                "programmer 1",
                "pg1@gmail.com",
                "JUNIOR",
                "DEVELOPER",
                List.of("project 1", "project 2", "project 3"),
                "department 1"
        );
        Programmer programmer = mapper.dtoToEntity(dto);

        assertNotNull(programmer);
        assertAll(
                () -> assertEquals(dto.getId(), programmer.getId()),
                () -> assertEquals(dto.getEmail(), programmer.getEmail()),
                () -> assertEquals(dto.getName(), programmer.getFullName()),
                () -> assertEquals(dto.getLevel(), programmer.getLevel().toString()),
                () -> assertEquals(dto.getType(), programmer.getType().toString()),
                () -> assertTrue(programmer.getProjects().isEmpty())
        );
    }
}
