package com.company.team_management.mapper;

import com.company.team_management.dto.TaskDTO;
import com.company.team_management.entities.Project;
import com.company.team_management.entities.Task;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {
    TaskDTO entityToDTO(Task task);
    @Mapping(target = "project", ignore = true)
    Task dtoToEntity(TaskDTO dto);

    List<TaskDTO> collectionToDTO(List<Task> tasks);

    default String statusToString(Task.Status status) {
        return status.toString();
    }
    default String projectToString(Project project) {
        return project.getTitle();
    }
}
