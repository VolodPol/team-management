package com.company.team_management.services.impl;

import com.company.team_management.entities.Programmer;
import com.company.team_management.exceptions.already_exists.EntityExistsException;
import com.company.team_management.repositories.ProgrammerRepository;
import com.company.team_management.services.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProgrammerService extends AbstractService<Programmer> {
    private final ProgrammerRepository programmerRepository;

    @Autowired
    public ProgrammerService(ProgrammerRepository repo) {
        this.programmerRepository = repo;
    }

    @CacheEvict(cacheNames = {"programmers", "bestProgrammers", "count"}, allEntries = true)
    @Transactional
    @Override
    public Programmer save(Programmer programmer) {
        Integer id = programmer.getId();
        if (id != null && programmerRepository.findById(id).orElse(null) != null) {
            throw new EntityExistsException("Employee already exists!");
        }
        return programmerRepository.save(programmer);
    }

    @Cacheable("programmers")
    @Transactional(readOnly = true)
    @Override
    public List<Programmer> findAll() {
        return programmerRepository.findAllFetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Programmer findById(int id) {
        return findIfPresent(id, programmerRepository::findByIdAndFetch);
    }

    @CacheEvict(cacheNames = {"programmers", "bestProgrammers", "count"}, allEntries = true)
    @Transactional
    @Override
    public void deleteById(int id) {
        findIfPresent(id, programmerRepository::findById);
        programmerRepository.deleteById(id);
    }

    @CacheEvict(cacheNames = "programmers", allEntries = true)
    @Transactional
    @Override
    public Programmer updateById(int id, Programmer programmer) {
        Programmer found = findIfPresent(id, programmerRepository::findByIdAndFetch);

        setNullable(found::setFullName, programmer.getFullName());
        setNullable(found::setEmail, programmer.getEmail());
        setNullable(found::setLevel, programmer.getLevel());
        setNullable(found::setType, programmer.getType());

        return found;
    }
}
