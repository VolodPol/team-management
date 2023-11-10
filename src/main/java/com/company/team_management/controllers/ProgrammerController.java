package com.company.team_management.controllers;

import com.company.team_management.dto.ProgrammerDto;
import com.company.team_management.dto.mapper.Mapper;
import com.company.team_management.dto.mapper.impl.ProgrammerMapper;
import com.company.team_management.entities.Programmer;
import com.company.team_management.exceptions.already_exists.ProgrammerAlreadyExistsException;
import com.company.team_management.exceptions.ErrorResponse;
import com.company.team_management.exceptions.no_such.NoSuchProgrammerException;
import com.company.team_management.services.impl.ProgrammerService;
import com.company.team_management.services.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("company")
public class ProgrammerController {
    private final IService<Programmer> service;
    private final Mapper<Programmer, ProgrammerDto> mapper;
    @Autowired
    public ProgrammerController(ProgrammerService service, ProgrammerMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping(value = "/programmer", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ProgrammerDto> addProgrammer(@RequestBody Programmer programmer) {
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

    @GetMapping(value = "/programmer/{id}", produces = "application/json")
    public ResponseEntity<ProgrammerDto> findById(@PathVariable int id) {
        Programmer foundEmp = service.findById(id);
        return new ResponseEntity<>(mapper.toDto(foundEmp),  HttpStatus.FOUND);
    }

    @DeleteMapping(value = "/programmer/{id}", produces = "application/json")
    public ResponseEntity<String> deleteById(@PathVariable int id) {
        service.deleteById(id);
        return new ResponseEntity<>(
                "Successfully deleted!", HttpStatus.NO_CONTENT
        );
    }

    @PutMapping(value = "/programmer/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ProgrammerDto> updateById(@PathVariable int id,
                                                    @RequestBody Programmer programmer) {
        ProgrammerDto dto = mapper.toDto(service.updateById(id, programmer));
        return new ResponseEntity<>(
                dto, HttpStatus.OK
        );
    }

    @ExceptionHandler(value = {ProgrammerAlreadyExistsException.class, NoSuchProgrammerException.class})
    public ErrorResponse handle(RuntimeException exception) {
        return new ErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
    }
}
