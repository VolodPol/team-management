package com.company.team_management.dto.mapper.impl;

import com.company.team_management.dto.TaskDTO;
import com.company.team_management.dto.mapper.Mapper;
import com.company.team_management.entities.Project;
import com.company.team_management.entities.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper extends Mapper<Task, TaskDTO> {
    @Override
    public TaskDTO toDto(Task entity) {
        Task.Status status = entity.getStatus();
        Project project = entity.getProject();

        return new TaskDTO(
                entity.getId(),
                entity.getName(),
                status == null ? Task.Status.ACTIVE.toString() : status.name(),
                project == null ? null : project.getTitle()
        );
    }

    @Override
    public Task fromDto(TaskDTO dto) {
        return new Task(
                dto.getId(),
                dto.getName(),
                Task.Status.valueOf(dto.getStatus()),
                null
        );
    }
}
