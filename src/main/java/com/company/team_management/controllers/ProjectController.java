package com.company.team_management.controllers;

import com.company.team_management.entities.Project;
import com.company.team_management.exceptions.ErrorResponse;
import com.company.team_management.exceptions.project.NoSuchProjectException;
import com.company.team_management.exceptions.project.ProjectAlreadyExistsException;
import com.company.team_management.services.IService;
import com.company.team_management.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProjectController {
    private final IService<Project> service;

    @Autowired
    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @GetMapping(value = "company/projects", produces = "application/json")
    public ResponseEntity<List<Project>> getAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "company/project/{id}", produces = "application/json")
    public ResponseEntity<Project> findById(@PathVariable int id) {
        return new ResponseEntity<>(service.findById(id), HttpStatus.FOUND);
    }

    @PostMapping(value = "company/project", consumes = "application/json")
    public ResponseEntity<Project> addProject(@RequestBody Project project) {
        return new ResponseEntity<>(service.save(project), HttpStatus.CREATED);
    }

    @DeleteMapping("company/project/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable int id) {
        service.deleteById(id);
        return new ResponseEntity<>("Successfully deleted!", HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "company/project/{id}", consumes = "application/json")
    public ResponseEntity<Project> updateProject(@PathVariable int id, @RequestBody Project updated) {
        return new ResponseEntity<>(service.updateById(id, updated), HttpStatus.OK);
    }

    @ExceptionHandler(value = {NoSuchProjectException.class, ProjectAlreadyExistsException.class})
    public ErrorResponse handle(RuntimeException exception) {
        return new ErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
    }
}
