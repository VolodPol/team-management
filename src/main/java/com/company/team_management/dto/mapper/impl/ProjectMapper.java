package com.company.team_management.dto.mapper.impl;

import com.company.team_management.dto.ProjectDTO;
import com.company.team_management.dto.mapper.Mapper;
import com.company.team_management.entities.Programmer;
import com.company.team_management.entities.Project;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectMapper extends Mapper<Project, ProjectDTO> {
    @Override
    public ProjectDTO toDto(Project project) {
        int id = project.getId();
        String title = project.getTitle();
        String goal = project.getGoal();
        String budget = String.valueOf(project.getBudget());
        List<String> employees = project.getProgrammers().stream()
                .map(Programmer::getFullName)
                .toList();

        return new ProjectDTO(id, title, goal, budget, employees);
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
