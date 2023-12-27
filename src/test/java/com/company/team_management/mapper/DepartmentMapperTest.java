package com.company.team_management.mapper;

import com.company.team_management.dto.DepartmentDto;
import com.company.team_management.entities.Department;
import com.company.team_management.entities.Programmer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DepartmentMapperImpl.class)
public class DepartmentMapperTest {
    @Autowired
    private DepartmentMapper mapper;

    @Test
    public void whenMapsFromEntityToDto_ThenCorrect() {
        Department dep = Department.builder()
                .id(1)
                .name("department 1")
                .location("Boston")
                .programmers(Set.of(
                        new Programmer.Builder()
                                .addFullName("programmer 1")
                                .build(),
                        new Programmer.Builder()
                                .addFullName("programmer 2")
                                .build()
                )).build();
        DepartmentDto dto = mapper.entityToDTO(dep);
        assertNotNull(dto);
        assertAll(
                () -> assertEquals(dto.getName(), dep.getName()),
                () -> assertEquals(dto.getAddress(), dep.getLocation()),
                () -> assertEquals(dto.getId(), dep.getId())
        );
        assertIterableEquals(dep.getProgrammers().stream()
                .map(Programmer::getFullName).toList(), dto.getProgrammers());
    }

    @Test
    public void whenMapsDtoToEntity_ThenCorrect() {
        DepartmentDto dto = new DepartmentDto(
                1,
                "department 1",
                "location 1",
                List.of("Jeremy", "Andrew", "Howard")
        );
        Department department = mapper.dtoToEntity(dto);

        assertNotNull(department);
        assertAll(
                () -> assertEquals(dto.getId(), department.getId()),
                () -> assertEquals(dto.getName(), department.getName()),
                () -> assertEquals(dto.getAddress(), department.getLocation()),
                () -> assertTrue(department.getProgrammers().isEmpty())
        );
    }
}
