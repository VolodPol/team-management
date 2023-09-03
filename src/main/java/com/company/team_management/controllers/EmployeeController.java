package com.company.team_management.controllers;

import com.company.team_management.dto.EmployeeDTO;
import com.company.team_management.dto.EmployeeMapper;
import com.company.team_management.entities.Employee;
import com.company.team_management.exceptions.employee.EmployeeAlreadyExistsException;
import com.company.team_management.exceptions.ErrorResponse;
import com.company.team_management.exceptions.employee.NoSuchEmployeeException;
import com.company.team_management.services.EmployeeService;
import com.company.team_management.services.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmployeeController {
    private final IService<Employee> service;
    private final EmployeeMapper mapper;
    @Autowired
    public EmployeeController(EmployeeService service, EmployeeMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping(value = "company/employee", consumes = "application/json")
    public ResponseEntity<EmployeeDTO> addEmployee(@RequestBody Employee employee) {
        Employee newEmployee = service.save(employee);
        return new ResponseEntity<>(mapper.toDTO(newEmployee), HttpStatus.CREATED);
    }

    @GetMapping(value = "company/employees", produces = "application/json")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        List<EmployeeDTO> dtoList = service.findAll().stream()
                .map(mapper::toDTO)
                .toList();
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @GetMapping(value = "company/employee/{id}", produces = "application/json")
    public ResponseEntity<EmployeeDTO> findById(@PathVariable int id) {
        Employee foundEmp = service.findById(id);
        return new ResponseEntity<>(mapper.toDTO(foundEmp),  HttpStatus.FOUND);
    }

    @DeleteMapping(value = "company/employee/{id}")
    public ResponseEntity<String> deleteById(@PathVariable int id) {
        service.deleteById(id);
        return new ResponseEntity<>(
                "Successfully deleted!", HttpStatus.NO_CONTENT
        );
    }

    @PutMapping(value = "company/employee/{id}", consumes = "application/json")
    public ResponseEntity<EmployeeDTO> updateById(@PathVariable int id,
                                               @RequestBody Employee employee) {
        EmployeeDTO dto = mapper.toDTO(service.updateById(id, employee));
        return new ResponseEntity<>(
                dto, HttpStatus.OK
        );
    }

    @ExceptionHandler(value = {EmployeeAlreadyExistsException.class, NoSuchEmployeeException.class})
    public ErrorResponse handle(RuntimeException exception) {
        return new ErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
    }
}
