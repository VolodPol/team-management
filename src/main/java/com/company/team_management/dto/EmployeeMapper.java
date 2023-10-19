package com.company.team_management.dto;

import com.company.team_management.entities.Employee;
import com.company.team_management.entities.Project;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmployeeMapper {
    public EmployeeDTO toDTO(Employee employee) {
        int id = employee.getId();
        String fullName = employee.getFullName();
        String email = employee.getEmail();
        String occupation = employee.getOccupation().toString();
        String level = employee.getLevel().toString();
        String type = employee.getType().toString();
        List<String> projects = employee.getProjects().stream()
                .map(Project::getTitle)
                .toList();

        return new EmployeeDTO(id, fullName, email, occupation, level, type, projects);
    }

    public Employee toEmployee(EmployeeDTO dto) {
        return new Employee.Builder()
                .addId(dto.getId())
                .addFullName(dto.getFullName())
                .addEmail(dto.getEmail())
                .addOccupation(Employee.Occupation.valueOf(dto.getOccupation()))
                .addLevel(Employee.Level.valueOf(dto.getLevel()))
                .addType(Employee.Type.valueOf(dto.getType()))
                .build();
    }
}
