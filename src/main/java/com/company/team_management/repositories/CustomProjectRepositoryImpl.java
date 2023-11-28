package com.company.team_management.repositories;

import com.company.team_management.entities.Project;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CustomProjectRepositoryImpl implements CustomProjectRepository {
    private final EntityManager manager;

    @Autowired
    public CustomProjectRepositoryImpl(EntityManager manager) {
        this.manager = manager;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Project> getAllFetchAllWithinBudget(Long lowerBound, Long upperBound) {
        List<Project> projects = manager.createQuery("select distinct pj from Project pj left join fetch pj.programmers " +
                        "where pj.budget between ?1 and ?2", Project.class)
                .setParameter(1, lowerBound)
                .setParameter(2, upperBound)
                .getResultList();

        projects = manager.createQuery("select distinct pj from Project pj left join fetch pj.tasks " +
                        "where pj in :collection", Project.class)
                .setParameter("collection", projects)
                .getResultList();

        return projects;
    }
}
