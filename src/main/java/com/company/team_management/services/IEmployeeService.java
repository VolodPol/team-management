package com.company.team_management.services;

import com.company.team_management.entities.Employee;

import java.util.List;

public interface IEmployeeService {
    Employee save(Employee employee);
    List<Employee> findAll();
    Employee findById(int id);
    Employee deleteById(int id);
    Employee updateById(int id, Employee employee);
}
