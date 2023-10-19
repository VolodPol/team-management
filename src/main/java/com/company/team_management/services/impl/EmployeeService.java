package com.company.team_management.services.impl;

import com.company.team_management.entities.Employee;
import com.company.team_management.exceptions.employee.EmployeeAlreadyExistsException;
import com.company.team_management.exceptions.employee.NoSuchEmployeeException;
import com.company.team_management.repositories.EmployeeRepository;
import com.company.team_management.services.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class EmployeeService implements IService<Employee> {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository repo) {
        this.employeeRepository = repo;
    }

    @Transactional
    @Override
    public Employee save(Employee employee) {
        Integer id = employee.getId();
        if (id != null && employeeRepository.findById(id).orElse(null) != null) {
            throw new EmployeeAlreadyExistsException("Employee already exists!");
        }
        return employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAllFetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Employee findById(int id) {
        return findIfPresent(id, employeeRepository::findByIdAndFetch);
    }

    @Transactional
    @Override
    public void deleteById(int id) {
        findIfPresent(id, employeeRepository::findById);
        employeeRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Employee updateById(int id, Employee employee) {
        Employee found = findIfPresent(id, employeeRepository::findByIdAndFetch);

        setNullable(found::setFullName, employee.getFullName());
        setNullable(found::setEmail, employee.getEmail());
        setNullable(found::setOccupation, employee.getOccupation());
        setNullable(found::setLevel, employee.getLevel());
        setNullable(found::setType, employee.getType());

        return found;
    }

    private Employee findIfPresent(int id, Function<Integer, Optional<Employee>> finder) {
        Optional<Employee> employee = finder.apply(id);
        if (employee.isEmpty())
            throw new NoSuchEmployeeException(
                    String.format("There is no employee with id = %d", id)
            );
        return employee.get();
    }
}
