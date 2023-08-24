package com.company.team_management.services;

import com.company.team_management.entities.Employee;
import com.company.team_management.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
public class EmployeeService implements IEmployeeService {
    private final EmployeeRepository repo;

    @Autowired
    public EmployeeService(EmployeeRepository repo) {
        this.repo = repo;
    }

    @Override
    public Employee save(Employee employee) {
        return repo.save(employee);
    }

    @Override
    public List<Employee> findAll() {
        return repo.findAll();
    }

    @Override
    public Employee findById(int id) {
        return repo.findById(id)
                .orElse(null);
    }

    @Override
    public Employee deleteById(int id) {
        Employee found = findById(id);

        repo.deleteById(id);
        return found;
    }

    @Override
    public Employee updateById(int id, Employee employee) {
        Employee found = findById(id);

        if (found != null) {
            setNullable(found::setFullName, employee.getFullName());
            setNullable(found::setEmail, employee.getEmail());
            setNullable(found::setOccupation, employee.getOccupation());
            setNullable(found::setLevel, employee.getLevel());
            setNullable(found::setType, employee.getType());

            repo.save(found);
        }

        return found;
    }

    private <T> void setNullable(Consumer<T> setter, T value) {
        if (value != null)
            setter.accept(value);
    }
}
