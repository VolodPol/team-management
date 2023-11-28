package com.company.team_management.controllers;

import com.company.team_management.dto.ProgrammerDto;
import com.company.team_management.dto.mapper.Mapper;
import com.company.team_management.dto.mapper.impl.ProgrammerMapper;
import com.company.team_management.entities.Programmer;
import com.company.team_management.services.StatisticsService;
import com.company.team_management.services.impl.ProgrammerService;
import com.company.team_management.services.IService;
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
public class ProgrammerController {
    private final IService<Programmer> service;
    private final StatisticsService statService;
    private final Mapper<Programmer, ProgrammerDto> mapper;
    @Autowired
    public ProgrammerController(ProgrammerService service, StatisticsService statService, ProgrammerMapper mapper) {
        this.service = service;
        this.statService = statService;
        this.mapper = mapper;
    }

    @Validated(value = CreateGroup.class)
    @PostMapping(value = "/programmer", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ProgrammerDto> addProgrammer(@Valid @RequestBody Programmer programmer) {
        Programmer newProgrammer = service.save(programmer);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(programmer.getId())
                .toUri();
        return ResponseEntity.created(location)
                .body(mapper.toDto(newProgrammer));
    }

    @GetMapping(value = "/programmers", produces = "application/json")
    public ResponseEntity<List<ProgrammerDto>> getAllProgrammers() {
        List<ProgrammerDto> dtoList = mapper.collectionToDto(service.findAll());
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @GetMapping(value = "/programmers/best", produces = "application/json")
    public ResponseEntity<List<ProgrammerDto>> getBest() {
        List<ProgrammerDto> dtoList = mapper.collectionToDto(statService.findMostSuccessful());
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @GetMapping(value = "/programmer/{id}", produces = "application/json")
    public ResponseEntity<ProgrammerDto> findById(@PathVariable @Min(0) int id) {
        Programmer foundEmp = service.findById(id);
        return new ResponseEntity<>(mapper.toDto(foundEmp),  HttpStatus.FOUND);
    }

    @DeleteMapping(value = "/programmer/{id}", produces = "application/json")
    public ResponseEntity<String> deleteById(@PathVariable @Min(0) int id) {
        service.deleteById(id);
        return new ResponseEntity<>(
                "Successfully deleted!", HttpStatus.NO_CONTENT
        );
    }

    @Validated(value = UpdateGroup.class)
    @PutMapping(value = "/programmer/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ProgrammerDto> updateById(@PathVariable @Min(0) int id,
                                                    @Valid @RequestBody Programmer programmer) {
        ProgrammerDto dto = mapper.toDto(service.updateById(id, programmer));
        return new ResponseEntity<>(
                dto, HttpStatus.OK
        );
    }
}
