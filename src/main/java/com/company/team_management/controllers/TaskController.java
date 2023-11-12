package com.company.team_management.controllers;

import com.company.team_management.dto.TaskDTO;
import com.company.team_management.dto.mapper.Mapper;
import com.company.team_management.dto.mapper.impl.TaskMapper;
import com.company.team_management.entities.Task;
import com.company.team_management.exceptions.ErrorResponse;
import com.company.team_management.exceptions.already_exists.TaskAlreadyExistsException;
import com.company.team_management.exceptions.no_such.NoSuchTaskException;
import com.company.team_management.services.IService;
import com.company.team_management.services.impl.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

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
    public ResponseEntity<TaskDTO> findById(@PathVariable int id) {
        TaskDTO dto = mapper.toDto(service.findById(id));
        return new ResponseEntity<>(dto, HttpStatus.FOUND);
    }

    @PostMapping(value = "/task", consumes = "application/json")
    public ResponseEntity<TaskDTO> addTask(@RequestBody Task task) {
        TaskDTO dto = mapper.toDto(service.save(task));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(task.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(dto);
    }

    @DeleteMapping("/task/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable int id) {
        service.deleteById(id);
        return new ResponseEntity<>("Successfully deleted!", HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/task/{id}", consumes = "application/json")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable int id, @RequestBody Task updated) {
        TaskDTO dto = mapper.toDto(service.updateById(id, updated));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @ExceptionHandler(value = {NoSuchTaskException.class, TaskAlreadyExistsException.class})
    public ErrorResponse handle(RuntimeException exception) {
        return new ErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
    }
}
