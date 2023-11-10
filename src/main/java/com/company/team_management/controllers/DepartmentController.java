package com.company.team_management.controllers;

import com.company.team_management.dto.DepartmentDto;
import com.company.team_management.dto.mapper.impl.DepartmentMapper;
import com.company.team_management.dto.mapper.Mapper;
import com.company.team_management.entities.Department;
import com.company.team_management.services.IService;
import com.company.team_management.services.impl.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("company")
public class DepartmentController {
    private final IService<Department> service;
    private final Mapper<Department, DepartmentDto> mapper;

    @Autowired
    public DepartmentController(DepartmentService service, DepartmentMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping(value = "/department", consumes = "application/json")
    public ResponseEntity<Department> saveDepartment(@RequestBody Department department) {
        service.save(department);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(department.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(department);
    }

    @GetMapping(value = "/departments", produces = "application/json")
    public ResponseEntity<List<DepartmentDto>> getAll() {
        List<DepartmentDto> departments = mapper.collectionToDto(service.findAll());
        return new ResponseEntity<>(departments, HttpStatus.OK);
    }

    @DeleteMapping(value = "/department/{id}", produces = "application/json")
    public ResponseEntity<String> removeById(@PathVariable int id) {
        service.deleteById(id);
        return new ResponseEntity<>("Successfully deleted!", HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/department/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<DepartmentDto> updateDepartment(@PathVariable int id,
                                                          @RequestBody Department department) {
        Department updated = service.updateById(id, department);
        DepartmentDto updateDto = mapper.toDto(updated);

        return ResponseEntity.ok(updateDto);
    }

    @GetMapping(value = "/department/{id}", produces = "application/json")
    public ResponseEntity<DepartmentDto> findDepartmentById(@PathVariable int id) {
        Department found = service.findById(id);
        DepartmentDto dto = mapper.toDto(found);

        return ResponseEntity.ok(dto);
    }
}
