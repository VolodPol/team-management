package com.company.team_management.mapper;

import com.company.team_management.dto.ProgrammerDto;
import com.company.team_management.entities.Department;
import com.company.team_management.entities.Programmer;
import com.company.team_management.entities.Project;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.FIELD,
        uses = DepartmentMapper.class)
public interface ProgrammerMapper {
    @Mapping(target = "name", source = "fullName")
    ProgrammerDto entityToDTO(Programmer programmer);

    @Mapping(target = "fullName", source = "name")
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "department", ignore = true)
    Programmer dtoToEntity(ProgrammerDto dto);

    List<ProgrammerDto> collectionToDTO(List<Programmer> programmers);


    default String projectToTitle(Project project) {
        return project.getTitle();
    }

    default String departmentToString(Department department) {
        return department.getName();
    }

    default String levelToString(Programmer.Level level) {
        return level.toString();
    }

    default String typeToString(Programmer.Type type) {
        return type.toString();
    }
}
