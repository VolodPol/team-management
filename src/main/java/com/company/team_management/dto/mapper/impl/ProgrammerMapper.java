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
        Integer id = programmer.getId();
        String fullName = programmer.getFullName();
        String email = programmer.getEmail();
        Programmer.Level level = programmer.getLevel();
        Programmer.Type type = programmer.getType();
        Set<Project> projects = programmer.getProjects();
        Department department = programmer.getDepartment();

        String levelName = level == null ? null : level.toString();
        String typeName = type == null ? null : type.toString();
        List<String> projectTitles = projects != null ?
                projects.stream().map(Project::getTitle).toList()
                : List.of();
        String depName = department == null ? null : department.getName();

        return new ProgrammerDto(id, fullName, email, levelName, typeName, projectTitles, depName);
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
