package com.company.team_management.controllers;

import com.company.team_management.dto.ProjectDTO;
import com.company.team_management.dto.mapper.Mapper;
import com.company.team_management.dto.mapper.impl.ProjectMapper;
import com.company.team_management.entities.Project;
import com.company.team_management.exceptions.ErrorResponse;
import com.company.team_management.exceptions.no_such.NoSuchProjectException;
import com.company.team_management.exceptions.already_exists.ProjectAlreadyExistsException;
import com.company.team_management.services.IService;
import com.company.team_management.services.impl.ProjectService;
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
public class ProjectController {
    private final IService<Project> service;
    private final Mapper<Project, ProjectDTO> mapper;

    @Autowired
    public ProjectController(ProjectService service, ProjectMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping(value = "/projects", produces = "application/json")
    public ResponseEntity<List<ProjectDTO>> getAll() {
        List<ProjectDTO> dtoList = mapper.collectionToDto(service.findAll());
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @GetMapping(value = "/project/{id}", produces = "application/json")
    public ResponseEntity<ProjectDTO> findById(@PathVariable @Min(0) int id) {
        ProjectDTO dto = mapper.toDto(service.findById(id));
        return new ResponseEntity<>(dto, HttpStatus.FOUND);
    }

    @PostMapping(value = "/project", consumes = "application/json")
    public ResponseEntity<ProjectDTO> addProject(@Valid @RequestBody Project project) {
        ProjectDTO dto = mapper.toDto(service.save(project));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(project.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(dto);
    }

    @DeleteMapping("/project/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable @Min(0) int id) {
        service.deleteById(id);
        return new ResponseEntity<>("Successfully deleted!", HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/project/{id}", consumes = "application/json")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable @Min(0) int id,
                                                    @Valid @RequestBody Project updated) {
        ProjectDTO dto = mapper.toDto(service.updateById(id, updated));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @ExceptionHandler(value = {NoSuchProjectException.class, ProjectAlreadyExistsException.class})
    public ErrorResponse handle(RuntimeException exception) {
        return new ErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
    }
}
