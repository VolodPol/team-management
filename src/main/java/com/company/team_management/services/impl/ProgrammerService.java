package com.company.team_management.services.impl;

import com.company.team_management.entities.Programmer;
import com.company.team_management.exceptions.already_exists.ProgrammerAlreadyExistsException;
import com.company.team_management.exceptions.no_such.NoSuchProgrammerException;
import com.company.team_management.repositories.ProgrammerRepository;
import com.company.team_management.services.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class ProgrammerService implements IService<Programmer> {
    private final ProgrammerRepository programmerRepository;

    @Autowired
    public ProgrammerService(ProgrammerRepository repo) {
        this.programmerRepository = repo;
    }

    @Transactional
    @Override
    public Programmer save(Programmer programmer) {
        Integer id = programmer.getId();
        if (id != null && programmerRepository.findById(id).orElse(null) != null) {
            throw new ProgrammerAlreadyExistsException("Employee already exists!");
        }
        return programmerRepository.save(programmer);
    }

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

    @Transactional
    @Override
    public void deleteById(int id) {
        findIfPresent(id, programmerRepository::findById);
        programmerRepository.deleteById(id);
    }

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

    private Programmer findIfPresent(int id, Function<Integer, Optional<Programmer>> finder) {
        Optional<Programmer> employee = finder.apply(id);
        if (employee.isEmpty())
            throw new NoSuchProgrammerException(
                    String.format("There is no employee with id = %d", id)
            );
        return employee.get();
    }
}
