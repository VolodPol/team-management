package com.company.team_management.dto.mapper.impl;

import com.company.team_management.dto.ProjectDTO;
import com.company.team_management.dto.mapper.Mapper;
import com.company.team_management.entities.Programmer;
import com.company.team_management.entities.Project;
import com.company.team_management.entities.Task;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectMapper extends Mapper<Project, ProjectDTO> {
    @Override
    public ProjectDTO toDto(Project project) {
        Integer id = project.getId();
        String title = project.getTitle();
        String goal = project.getGoal();
        String budget = String.valueOf(project.getBudget());
        List<String> programmers = project.getProgrammers().stream()
                .map(Programmer::getFullName)
                .toList();
        List<String> tasks = project.getTasks().stream()
                .map(Task::getName)
                .toList();

        return new ProjectDTO(id, title, goal, budget, programmers, tasks);
    }

    @Override
    public Project fromDto(ProjectDTO dto) {
        return new Project.Builder()
                .addId(dto.getId())
                .addTitle(dto.getTitle())
                .addGoal(dto.getGoal())
                .addBudget(Long.valueOf(dto.getBudget()))
                .build();
    }
}
