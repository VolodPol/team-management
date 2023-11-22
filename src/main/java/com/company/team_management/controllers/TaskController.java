package com.company.team_management.controllers;

import com.company.team_management.dto.TaskDTO;
import com.company.team_management.dto.mapper.Mapper;
import com.company.team_management.dto.mapper.impl.TaskMapper;
import com.company.team_management.entities.Task;
import com.company.team_management.services.IService;
import com.company.team_management.services.impl.TaskService;
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
public class TaskController {
    private final IService<Task> service;
    private final Mapper<Task, TaskDTO> mapper;

    @Autowired
    public TaskController(TaskService service, TaskMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping(value = "/tasks", produces = "application/json")
    public ResponseEntity<List<TaskDTO>> getAll() {
        List<TaskDTO> dtoList = mapper.collectionToDto(service.findAll());
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @GetMapping(value = "/task/{id}", produces = "application/json")
    public ResponseEntity<TaskDTO> findById(@PathVariable @Min(0) int id) {
        TaskDTO dto = mapper.toDto(service.findById(id));
        return new ResponseEntity<>(dto, HttpStatus.FOUND);
    }

    @Validated(value = CreateGroup.class)
    @PostMapping(value = "/task", consumes = "application/json")
    public ResponseEntity<TaskDTO> addTask(@Valid @RequestBody Task task) {
        TaskDTO dto = mapper.toDto(service.save(task));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(task.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(dto);
    }

    @DeleteMapping("/task/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable @Min(0) int id) {
        service.deleteById(id);
        return new ResponseEntity<>("Successfully deleted!", HttpStatus.NO_CONTENT);
    }

    @Validated(value = UpdateGroup.class)
    @PutMapping(value = "/task/{id}", consumes = "application/json")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable @Min(0) int id, @Valid @RequestBody Task updated) {
        TaskDTO dto = mapper.toDto(service.updateById(id, updated));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
