package com.company.team_management.dto;

import com.company.team_management.entities.Employee;
import com.company.team_management.entities.Project;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectMapper {
    public ProjectDTO toDTO(Project project) {
        int id = project.getId();
        String title = project.getTitle();
        String goal = project.getGoal();
        String budget = String.valueOf(project.getBudget());
        boolean finished = project.getFinished();
        List<String> employees = project.getEmployees().stream()
                .map(Employee::getFullName)
                .toList();

        return new ProjectDTO(id, title, goal, budget, finished, employees);
    }

    public Project toProject(ProjectDTO dto) {
        return new Project.Builder()
                .addId(dto.getId())
                .addTitle(dto.getTitle())
                .addGoal(dto.getGoal())
                .addBudget(Long.valueOf(dto.getBudget()))
                .addFinished(dto.isFinished())
                .build();
    }
}
