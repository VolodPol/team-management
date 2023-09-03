package com.company.team_management.services;

import com.company.team_management.entities.Project;
import com.company.team_management.exceptions.project.NoSuchProjectException;
import com.company.team_management.exceptions.project.ProjectAlreadyExistsException;
import com.company.team_management.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService implements IService<Project> {
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Project save(Project project) {
        if (project.getId() == null)
            return projectRepository.save(project);

        Project found = projectRepository.findById(project.getId()).orElse(null);
        if (found != null)
            throw new ProjectAlreadyExistsException("Project already exists!");

        return projectRepository.save(project);
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public Project findById(int id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NoSuchProjectException(
                        String.format("There is no project with id = %d", id)
                ));
    }

    @Override
    public void deleteById(int id) {
        findById(id);
        projectRepository.deleteById(id);
    }

    @Override
    public Project updateById(int id, Project project) {
        Project found = findById(id);

        setNullable(found::setTitle, project.getTitle());
        setNullable(found::setGoal, project.getGoal());
        setNullable(found::setBudget, project.getBudget());
        setNullable(found::setFinished, project.getFinished());

        return projectRepository.save(found);
    }
}