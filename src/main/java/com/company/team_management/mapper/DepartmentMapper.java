package com.company.team_management.mapper;

import com.company.team_management.dto.DepartmentDto;
import com.company.team_management.entities.Department;
import com.company.team_management.entities.Programmer;
import org.mapstruct.*;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DepartmentMapper {
    @Mapping(target = "address", source = "location")
    DepartmentDto entityToDTO(Department department);
    @Mapping(target = "location", source = "address")
    @Mapping(target = "programmers", ignore = true)
    Department dtoToEntity(DepartmentDto dto);

    List<DepartmentDto> collectionToDTO(List<Department> departments);

    default String programmerToName(Programmer programmer) {
        return programmer.getFullName();
    }
}
