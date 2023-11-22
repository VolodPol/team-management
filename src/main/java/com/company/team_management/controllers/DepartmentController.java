package com.company.team_management.controllers;

import com.company.team_management.dto.DepartmentDto;
import com.company.team_management.dto.mapper.impl.DepartmentMapper;
import com.company.team_management.dto.mapper.Mapper;
import com.company.team_management.entities.Department;
import com.company.team_management.services.IService;
import com.company.team_management.services.StatisticsService;
import com.company.team_management.services.impl.DepartmentService;
import com.company.team_management.validation.CreateGroup;
import com.company.team_management.validation.UpdateGroup;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping("company")
public class DepartmentController {
    private final IService<Department> service;
    private final StatisticsService statService;
    private final Mapper<Department, DepartmentDto> mapper;

    @Autowired
    public DepartmentController(DepartmentService service, StatisticsService statService, DepartmentMapper mapper) {
        this.service = service;
        this.statService = statService;
        this.mapper = mapper;
    }

    @Validated(value = CreateGroup.class)
    @PostMapping(value = "/department", consumes = "application/json")
    public ResponseEntity<Department> saveDepartment(@Valid @RequestBody Department department) {
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
    public ResponseEntity<String> removeById(@PathVariable @Min(0) int id) {
        service.deleteById(id);
        return new ResponseEntity<>("Successfully deleted!", HttpStatus.NO_CONTENT);
    }

    @Validated(value = UpdateGroup.class)
    @PutMapping(value = "/department/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<DepartmentDto> updateDepartment(@PathVariable @Min(0) int id,
                                                          @Valid @RequestBody Department department) {
        Department updated = service.updateById(id, department);
        DepartmentDto updateDto = mapper.toDto(updated);

        return ResponseEntity.ok(updateDto);
    }

    @GetMapping(value = "/department/{id}", produces = "application/json")
    public ResponseEntity<DepartmentDto> findDepartmentById(@PathVariable @Min(0) int id) {
        Department found = service.findById(id);
        DepartmentDto dto = mapper.toDto(found);

        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/department/programmers", produces = "application/json")
    public ResponseEntity<String> getCountStatistics() {
        return ResponseEntity.ok(statService.countProgrammersPerDepartment());
    }
}
