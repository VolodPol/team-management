package com.company.team_management.controllers;

import com.company.team_management.dto.ProjectDTO;
import com.company.team_management.entities.Project;
import com.company.team_management.mapper.EntityMapper;
import com.company.team_management.mapper.ProjectMapper;
import com.company.team_management.services.IService;
import com.company.team_management.services.StatisticsService;
import com.company.team_management.services.impl.ProjectService;
import com.company.team_management.validation.CreateGroup;
import com.company.team_management.validation.UpdateGroup;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ProjectController {
    private final IService<Project> service;
    private final StatisticsService statisticsService;
    private final EntityMapper<Project, ProjectDTO> mapper;

    @GetMapping(value = "/projects", produces = "application/json")
    public ResponseEntity<List<ProjectDTO>> getAll() {
        List<ProjectDTO> dtoList = mapper.collectionToDTO(service.findAll());
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @GetMapping(value = "/project/{id}", produces = "application/json")
    public ResponseEntity<ProjectDTO> findById(@PathVariable @Min(0) int id) {
        ProjectDTO dto = mapper.entityToDTO(service.findById(id));
        return new ResponseEntity<>(dto, HttpStatus.FOUND);
    }

    @Validated(value = CreateGroup.class)
    @PostMapping(value = "/project", consumes = "application/json")
    public ResponseEntity<ProjectDTO> addProject(@Valid @RequestBody Project project) {
        ProjectDTO dto = mapper.entityToDTO(service.save(project));
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

    @Validated(value = UpdateGroup.class)
    @PutMapping(value = "/project/{id}", consumes = "application/json")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable @Min(0) int id,
                                                    @Valid @RequestBody Project updated) {
        ProjectDTO dto = mapper.entityToDTO(service.updateById(id, updated));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping(value = "/projects/budget", produces = "application/json")
    public List<Project> getProjectsWithinBudget(@RequestParam(name = "lower") @Min(0) long lowerBound,
                                                 @RequestParam(name = "upper") @Min(0) long upperBound) {
        return statisticsService.getProjectsWithInfoWithinBudget(lowerBound, upperBound);
    }
}
