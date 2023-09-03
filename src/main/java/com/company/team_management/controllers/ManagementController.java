package com.company.team_management.controllers;

import com.company.team_management.entities.Employee;
import com.company.team_management.services.ManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ManagementController {
    private final ManagementService managementService;

    @PostMapping(value = "company/manage/addProject/{id}")
    public ResponseEntity<String> addEmployeeToProject(@PathVariable(name = "id") int projectId,
                                                         @RequestBody Employee employee) {
        Employee saved = managementService.addEmpToProject(projectId, employee);
        return new ResponseEntity<>(saved.toString(), HttpStatus.CREATED);
    }
}
