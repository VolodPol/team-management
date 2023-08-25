package com.company.team_management.controllers;

import com.company.team_management.entities.Employee;
import com.company.team_management.exceptions.EmployeeAlreadyExistsException;
import com.company.team_management.exceptions.ErrorResponse;
import com.company.team_management.exceptions.NoSuchEmployeeException;
import com.company.team_management.services.EmployeeService;
import com.company.team_management.services.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmployeeController {
    private final IEmployeeService service;
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

    @GetMapping(value = "company/employee/{id}", produces = "application/json")
    public ResponseEntity<Employee> findById(@PathVariable int id) {
        Employee foundEmp = service.findById(id);
        return new ResponseEntity<>(foundEmp, foundEmp == null
                ? HttpStatus.NOT_FOUND
                : HttpStatus.FOUND);
    }

    @DeleteMapping(value = "company/employee/{id}")
    public ResponseEntity<Employee> deleteById(@PathVariable int id) {
        service.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "company/employee/{id}", consumes = "application/json")
    public ResponseEntity<Employee> updateById(@PathVariable int id, @RequestBody Employee employee) {
        return new ResponseEntity<>(
                service.updateById(id, employee), HttpStatus.OK
        );
    }

    @ExceptionHandler(value = {EmployeeAlreadyExistsException.class, NoSuchEmployeeException.class})
    public ErrorResponse handle(RuntimeException exception) {
        return new ErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
    }
}
