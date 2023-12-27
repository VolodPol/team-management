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
public abstract class ProgrammerMapper implements EntityMapper<Programmer, ProgrammerDto> {
    @Override
    @Mapping(target = "name", source = "fullName")
    public abstract ProgrammerDto entityToDTO(Programmer programmer);

    @Override
    @Mapping(target = "fullName", source = "name")
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "department", ignore = true)
    public abstract Programmer dtoToEntity(ProgrammerDto dto);

    @Override
    public abstract List<ProgrammerDto> collectionToDTO(List<Programmer> programmers);

    @Override
    public abstract List<Programmer> collectionFromDTO(List<ProgrammerDto> dtoList);

    protected String projectToTitle(Project project) {
        return project.getTitle();
    }

    protected String departmentToString(Department department) {
        return department.getName();
    }

    protected String levelToString(Programmer.Level level) {
        return level.toString();
    }

    protected String typeToString(Programmer.Type type) {
        return type.toString();
    }
}
