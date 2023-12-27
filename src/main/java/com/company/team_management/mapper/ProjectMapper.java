package com.company.team_management.mapper;

import com.company.team_management.dto.ProjectDTO;
import com.company.team_management.dto.TaskDTO;
import com.company.team_management.entities.Programmer;
import com.company.team_management.entities.Project;
import com.company.team_management.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = TaskMapper.class)
public abstract class ProjectMapper implements EntityMapper<Project, ProjectDTO> {
    @Override
    public abstract ProjectDTO entityToDTO(Project project);
    @Override
    @Mapping(target = "programmers", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    public abstract Project dtoToEntity(ProjectDTO dto);

    @Override
    public abstract List<ProjectDTO> collectionToDTO(List<Project> projects);

    @Override
    public abstract List<Project> collectionFromDTO(List<ProjectDTO> dtoList);

    protected String budgetToString(Long budget) {
        return String.valueOf(budget);
    }

    protected String programmerToName(Programmer programmer) {
        return programmer.getFullName();
    }
}