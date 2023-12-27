package com.company.team_management.mapper;

import com.company.team_management.dto.DepartmentDto;
import com.company.team_management.entities.Department;
import com.company.team_management.entities.Programmer;
import org.mapstruct.*;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class DepartmentMapper implements EntityMapper<Department, DepartmentDto> {
    @Override
    @Mapping(target = "address", source = "location")
    public abstract DepartmentDto entityToDTO(Department department);
    @Override
    @Mapping(target = "location", source = "address")
    @Mapping(target = "programmers", ignore = true)
    public abstract Department dtoToEntity(DepartmentDto dto);

    @Override
    public abstract List<DepartmentDto> collectionToDTO(List<Department> departments);
    @Override
    public abstract List<Department> collectionFromDTO(List<DepartmentDto> dtoList);

    protected String programmerToName(Programmer programmer) {
        return programmer.getFullName();
    }
}
