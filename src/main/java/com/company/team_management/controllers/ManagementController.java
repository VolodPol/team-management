package com.company.team_management.controllers;

import com.company.team_management.dto.ProgrammerDto;
import com.company.team_management.dto.mapper.Mapper;
import com.company.team_management.entities.Programmer;
import com.company.team_management.services.impl.ManagementService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Validated
@RestController
@RequestMapping("company")
@RequiredArgsConstructor
public class ManagementController {
    private final ManagementService managementService;
    private final Mapper<Programmer, ProgrammerDto> mapper;

    @PostMapping(value = "/manage/addProject/{id}",
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<ProgrammerDto> addNewProgrammerToProject(@PathVariable(name = "id") @Min(0) int projectId,
                                                                   @Valid @RequestBody Programmer programmer) {
        Programmer saved = managementService.addNewProgrammerToProject(projectId, programmer);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(programmer.getId())
                .toUri();
        return ResponseEntity.created(location)
                .body(mapper.toDto(saved));
    }

    @PostMapping(value = "/manage/addProject", produces = "application/json")
    public ResponseEntity<ProgrammerDto> addProgrammerToProject(@RequestParam(name = "programmer") @Min(0) int empId,
                                                                @RequestParam(name = "project") @Min(0) int projectId) {
        Programmer updated = managementService.addProgrammerByIdToProject(empId, projectId);
        ProgrammerDto dto = mapper.toDto(updated);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping(value="/manage/removeProject")
    public ResponseEntity<String> removeProject(@RequestParam(name = "programmer") @Min(0) int empId,
                                                @RequestParam(name = "project") @Min(0) int projectId) {
        managementService.removeProjectFromProgrammer(empId, projectId);
        return ResponseEntity.ok(
                String.format("Project with id = %d was deleted from user with id = %d", projectId, empId)
        );
    }
}
