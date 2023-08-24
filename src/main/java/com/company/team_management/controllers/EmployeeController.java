package com.company.team_management.controllers;

import com.company.team_management.entities.Employee;
import com.company.team_management.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmployeeController {
    private final EmployeeService service;
    @Autowired
    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @PostMapping(value = "company/employee", consumes = "application/json")
    public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee) {
        Employee newEmployee = service.save(employee);
        return new ResponseEntity<>(newEmployee, HttpStatus.CREATED);
    }

    @GetMapping(value = "company/employees", produces = "application/json")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return new ResponseEntity<>(
                service.findAll(), HttpStatus.OK
        );
    }

    @GetMapping(value = "company/employee", produces = "application/json")
    public ResponseEntity<Employee> findById(@RequestParam(name = "id") int id) {
        Employee foundEmp = service.findById(id);
        return new ResponseEntity<>(foundEmp, foundEmp == null
                ? HttpStatus.NOT_FOUND
                : HttpStatus.FOUND);
    }

    @DeleteMapping(value = "company/employee", params = "id")
    public ResponseEntity<Employee> deleteById(@RequestParam int id) {
        return new ResponseEntity<>(service.deleteById(id), HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "company/employee", consumes = "application/json")
    public ResponseEntity<Employee> updateById(@RequestParam int id, @RequestBody Employee employee) {
        return new ResponseEntity<>(
                service.updateById(id, employee), HttpStatus.OK
        );
    }
}
