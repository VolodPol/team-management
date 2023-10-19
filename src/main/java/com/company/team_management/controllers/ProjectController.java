package com.company.team_management.controllers;

import com.company.team_management.dto.ProjectDTO;
import com.company.team_management.dto.ProjectMapper;
import com.company.team_management.entities.Project;
import com.company.team_management.exceptions.ErrorResponse;
import com.company.team_management.exceptions.project.NoSuchProjectException;
import com.company.team_management.exceptions.project.ProjectAlreadyExistsException;
import com.company.team_management.services.IService;
import com.company.team_management.services.impl.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProjectController {
    private final IService<Project> service;
    private final ProjectMapper mapper;

    @Autowired
    public ProjectController(ProjectService service, ProjectMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping(value = "company/projects", produces = "application/json")
    public ResponseEntity<List<ProjectDTO>> getAll() {
        List<ProjectDTO> dtoList = service.findAll().stream()
                .map(mapper::toDTO)
                .toList();
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @GetMapping(value = "company/project/{id}", produces = "application/json")
    public ResponseEntity<ProjectDTO> findById(@PathVariable int id) {
        ProjectDTO dto = mapper.toDTO(service.findById(id));
        return new ResponseEntity<>(dto, HttpStatus.FOUND);
    }

    @PostMapping(value = "company/project", consumes = "application/json")
    public ResponseEntity<ProjectDTO> addProject(@RequestBody Project project) {
        ProjectDTO dto = mapper.toDTO(service.save(project));
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @DeleteMapping("company/project/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable int id) {
        service.deleteById(id);
        return new ResponseEntity<>("Successfully deleted!", HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "company/project/{id}", consumes = "application/json")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable int id, @RequestBody Project updated) {
        ProjectDTO dto = mapper.toDTO(service.updateById(id, updated));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @ExceptionHandler(value = {NoSuchProjectException.class, ProjectAlreadyExistsException.class})
    public ErrorResponse handle(RuntimeException exception) {
        return new ErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
    }
}
