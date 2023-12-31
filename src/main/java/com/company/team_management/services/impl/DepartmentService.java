package com.company.team_management.services.impl;

import com.company.team_management.entities.Department;
import com.company.team_management.exceptions.already_exists.EntityExistsException;
import com.company.team_management.repositories.DepartmentRepository;
import com.company.team_management.services.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentService extends AbstractService<Department> {
    private final DepartmentRepository repository;

    @Autowired
    public DepartmentService(DepartmentRepository repository) {
        this.repository = repository;
    }

    @CacheEvict(cacheNames = {"departments", "count"}, allEntries = true)
    @Transactional
    @Override
    public Department save(Department department) {
        Integer id = department.getId();
        if (id != null && repository.findByIdFetch(id).orElse(null) != null) {
            throw new EntityExistsException("Department already exists!");
        }
        return repository.save(department);
    }

    @Cacheable("departments")
    @Transactional(readOnly = true)
    @Override
    public List<Department> findAll() {
        return repository.findAllFetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Department findById(int id) {
        return findIfPresent(id, repository::findByIdFetch);
    }

    @CacheEvict(cacheNames = {"departments", "count"}, allEntries = true)
    @Transactional
    @Override
    public void deleteById(int id) {
        findIfPresent(id, repository::findByIdFetch);
        repository.deleteById(id);
    }

    @CacheEvict(cacheNames = "departments", allEntries = true)
    @Transactional
    @Override
    public Department updateById(int id, Department department) {
        Department found = findIfPresent(id, repository::findByIdFetch);

        setNullable(found::setName, department.getName());
        setNullable(found::setLocation, department.getLocation());

        return found;
    }
}
