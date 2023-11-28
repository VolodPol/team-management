package com.company.team_management.dto.mapper.impl;

import com.company.team_management.dto.DepartmentDto;
import com.company.team_management.dto.mapper.Mapper;
import com.company.team_management.entities.Department;
import com.company.team_management.entities.Programmer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DepartmentMapper extends Mapper<Department, DepartmentDto> {
    @Override
    public DepartmentDto toDto(Department entity) {
        List<String> programmerNames = entity.getProgrammers().stream()
                .map(Programmer::getFullName)
                .toList();
        return new DepartmentDto(
                entity.getId(),
                entity.getName(),
                entity.getLocation(),
                programmerNames
        );
    }

    @Override
    public Department fromDto(DepartmentDto dto) {
        Department department = new Department();
        department.setId(dto.getId());
        department.setName(dto.getName());
        department.setLocation(dto.getLocation());

        return department;
    }
}
