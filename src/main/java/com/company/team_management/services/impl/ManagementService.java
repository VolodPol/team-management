package com.company.team_management.services.impl;

import com.company.team_management.entities.Employee;
import com.company.team_management.entities.Project;
import com.company.team_management.services.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ManagementService {
    private final IService<Employee> empService;
    private final IService<Project> projectService;

    @Autowired
    public ManagementService(EmployeeService empService, ProjectService projectService) {
        this.empService = empService;
        this.projectService = projectService;
    }

    @Transactional
    public Employee addNewEmpToProject(int projectId, Employee employee) {
        Project project = projectService.findById(projectId);
        employee.addProject(project);
        empService.save(employee);

        return employee;
    }

    @Transactional
    public Employee addEmpByIdToProject(int employeeId, int projectId) {
        Employee emp = empService.findById(employeeId);
        Project project = projectService.findById(projectId);

        emp.addProject(project);
        return emp;
    }

    @Transactional
    public void removeProjectFromEmployee(int employeeId, int projectId) {
        Employee emp = empService.findById(employeeId);
        Project project = projectService.findById(projectId);

        emp.removeProject(project);
    }
}
