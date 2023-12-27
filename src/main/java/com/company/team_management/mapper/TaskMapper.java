package com.company.team_management.mapper;

import com.company.team_management.dto.TaskDTO;
import com.company.team_management.entities.Project;
import com.company.team_management.entities.Task;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class TaskMapper implements EntityMapper<Task, TaskDTO> {
    @Override
    public abstract TaskDTO entityToDTO(Task task);
    @Override
    @Mapping(target = "project", ignore = true)
    public abstract Task dtoToEntity(TaskDTO dto);

    @Override
    public abstract List<TaskDTO> collectionToDTO(List<Task> tasks);
    @Override
    public abstract List<Task> collectionFromDTO(List<TaskDTO> dtoList);

    protected String statusToString(Task.Status status) {
        return status.toString();
    }
    protected String projectToString(Project project) {
        return project.getTitle();
    }
}
