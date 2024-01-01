package com.company.team_management.mapper;

import com.company.team_management.dto.ProjectDTO;
import com.company.team_management.entities.Programmer;
import com.company.team_management.entities.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = TaskMapper.class)
public interface ProjectMapper {
    ProjectDTO entityToDTO(Project project);
    @Mapping(target = "programmers", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    Project dtoToEntity(ProjectDTO dto);

    List<ProjectDTO> collectionToDTO(List<Project> projects);

    default String budgetToString(Long budget) {
        return String.valueOf(budget);
    }

    default String programmerToName(Programmer programmer) {
        return programmer != null ? programmer.getFullName() : null;
    }
}