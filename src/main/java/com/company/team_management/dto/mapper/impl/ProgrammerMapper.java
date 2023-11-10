package com.company.team_management.dto.mapper.impl;

import com.company.team_management.dto.ProgrammerDto;
import com.company.team_management.dto.mapper.Mapper;
import com.company.team_management.entities.Department;
import com.company.team_management.entities.Programmer;
import com.company.team_management.entities.Project;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class ProgrammerMapper extends Mapper<Programmer, ProgrammerDto> {
    @Override
    public ProgrammerDto toDto(Programmer programmer) {
        int id = programmer.getId();
        String fullName = programmer.getFullName();
        String email = programmer.getEmail();
        String level = programmer.getLevel().toString();
        String type = programmer.getType().toString();
        Set<Project> projects = programmer.getProjects();
        List<String> projectTitles = projects != null ?
                projects.stream().map(Project::getTitle).toList()
                : List.of();
        Department department = programmer.getDepartment();
        String depName = department == null ? null : department.getName();

        return new ProgrammerDto(id, fullName, email, level, type, projectTitles, depName);
    }

    @Override
    public Programmer fromDto(ProgrammerDto dto) {
        return new Programmer.Builder()
                .addId(dto.getId())
                .addFullName(dto.getFullName())
                .addEmail(dto.getEmail())
                .addLevel(Programmer.Level.valueOf(dto.getLevel()))
                .addType(Programmer.Type.valueOf(dto.getType()))
                .build();
    }
}
