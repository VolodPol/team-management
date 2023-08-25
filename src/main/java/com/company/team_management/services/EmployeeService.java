package com.company.team_management.services;

import com.company.team_management.entities.Employee;
import com.company.team_management.exceptions.EmployeeAlreadyExistsException;
import com.company.team_management.exceptions.NoSuchEmployeeException;
import com.company.team_management.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
public class EmployeeService implements IEmployeeService {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository repo) {
        this.employeeRepository = repo;
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee findById(int id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchEmployeeException(
                        String.format("There is no employee with id = %d", id)
                ));
    }

    @Override
    public Employee save(Employee employee) {
        if (employee.getId() == null)
            return employeeRepository.save(employee);

        Employee found = employeeRepository.findById(employee.getId())
                .orElse(null);

        if (found != null)
            throw new EmployeeAlreadyExistsException("Employee already exists!");

        return employeeRepository.save(employee);
    }

    @Override
    public void deleteById(int id) {
        findById(id);
        employeeRepository.deleteById(id);
    }

    @Override
    public Employee updateById(int id, Employee employee) {
        Employee found = findById(id);

        setNullable(found::setFullName, employee.getFullName());
        setNullable(found::setEmail, employee.getEmail());
        setNullable(found::setOccupation, employee.getOccupation());
        setNullable(found::setLevel, employee.getLevel());
        setNullable(found::setType, employee.getType());

        return employeeRepository.save(found);
    }

    private <T> void setNullable(Consumer<T> setter, T value) {
        if (value != null)
            setter.accept(value);
    }
}