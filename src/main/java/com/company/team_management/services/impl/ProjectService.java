package com.company.team_management.services.impl;

import com.company.team_management.entities.Project;
import com.company.team_management.exceptions.no_such.NoSuchProjectException;
import com.company.team_management.exceptions.already_exists.ProjectAlreadyExistsException;
import com.company.team_management.repositories.ProjectRepository;
import com.company.team_management.services.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class ProjectService implements IService<Project> {
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    @Override
    public Project save(Project project) {
        Integer id = project.getId();
        if (id != null && projectRepository.findById(id).orElse(null) != null) {
            throw new ProjectAlreadyExistsException("Project already exists!");
        }
        return projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Project> findAll() {
        return projectRepository.findAllFetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Project findById(int id) {
        return findIfPresent(id, projectRepository::findByIdFetch);
    }

    @Transactional
    @Override
    public void deleteById(int id) {
        findIfPresent(id, projectRepository::findById);
        projectRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Project updateById(int id, Project project) {
        Project found = findIfPresent(id, projectRepository::findByIdFetch);

        setNullable(found::setTitle, project.getTitle());
        setNullable(found::setGoal, project.getGoal());
        setNullable(found::setBudget, project.getBudget());

        return found;
    }

    private Project findIfPresent(int id, Function<Integer, Optional<Project>> finder) {
        return finder.apply(id)
                .orElseThrow(
                        () -> new NoSuchProjectException(String.format("There is no project with id = %d", id))
                );
    }
}