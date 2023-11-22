package com.company.team_management.services.impl;

import com.company.team_management.entities.Project;
import com.company.team_management.exceptions.already_exists.EntityExistsException;
import com.company.team_management.repositories.ProjectRepository;
import com.company.team_management.services.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService extends AbstractService<Project> {
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @CacheEvict(cacheNames = {"projects", "bestProgrammers"}, allEntries = true)
    @Transactional
    @Override
    public Project save(Project project) {
        Integer id = project.getId();
        if (id != null && projectRepository.findById(id).orElse(null) != null) {
            throw new EntityExistsException("Project already exists!");
        }
        return projectRepository.save(project);
    }

    @Cacheable("projects")
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

    @CacheEvict(cacheNames = {"projects", "bestProgrammers"}, allEntries = true)
    @Transactional
    @Override
    public void deleteById(int id) {
        findIfPresent(id, projectRepository::findById);
        projectRepository.deleteById(id);
    }

    @CacheEvict(cacheNames = "projects", allEntries = true)
    @Transactional
    @Override
    public Project updateById(int id, Project project) {
        Project found = findIfPresent(id, projectRepository::findByIdFetch);

        setNullable(found::setTitle, project.getTitle());
        setNullable(found::setGoal, project.getGoal());
        setNullable(found::setBudget, project.getBudget());

        return found;
    }
}