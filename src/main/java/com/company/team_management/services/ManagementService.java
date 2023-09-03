package com.company.team_management.services;

import com.company.team_management.entities.Employee;
import com.company.team_management.entities.Project;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagementService {
    private final IService<Employee> empService;
    private final IService<Project> projectService;

    @Autowired
    public ManagementService(EmployeeService empService, ProjectService projectService) {
        this.empService = empService;
        this.projectService = projectService;
    }

    public Employee addEmpToProject(int projectId, Employee employee) {
        Project project = projectService.findById(projectId);
        employee.addProject(project);
        empService.save(employee);

        return employee;
    }
}
