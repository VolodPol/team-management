package com.company.team_management.services.impl;

import com.company.team_management.entities.Programmer;
import com.company.team_management.entities.Project;
import com.company.team_management.services.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ManagementService {
    private final IService<Programmer> empService;
    private final IService<Project> projectService;

    @Autowired
    public ManagementService(ProgrammerService programmerService, ProjectService projectService) {
        this.empService = programmerService;
        this.projectService = projectService;
    }

    @Transactional
    public Programmer addNewProgrammerToProject(int projectId, Programmer programmer) {
        Project project = projectService.findById(projectId);
        programmer.addProject(project);
        empService.save(programmer);

        return programmer;
    }

    @Transactional
    public Programmer addProgrammerByIdToProject(int programmerId, int projectId) {
        Programmer emp = empService.findById(programmerId);
        Project project = projectService.findById(projectId);

        emp.addProject(project);
        return emp;
    }

    @Transactional
    public void removeProjectFromProgrammer(int programmerId, int projectId) {
        Programmer emp = empService.findById(programmerId);
        Project project = projectService.findById(projectId);

        emp.removeProject(project);
    }
}
