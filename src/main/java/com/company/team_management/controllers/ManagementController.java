package com.company.team_management.controllers;

import com.company.team_management.dto.EmployeeDTO;
import com.company.team_management.dto.EmployeeMapper;
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
    private final EmployeeMapper mapper;

    @PostMapping(value = "company/manage/addProject/{id}",
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<EmployeeDTO> addNewEmployeeToProject(@PathVariable(name = "id") int projectId,
                                                               @RequestBody Employee employee) {
        Employee saved = managementService.addNewEmpToProject(projectId, employee);
        return new ResponseEntity<>(mapper.toDTO(saved), HttpStatus.CREATED);
    }

    @PostMapping(value = "company/manage/addProject", produces = "application/json")
    public ResponseEntity<EmployeeDTO> addEmployeeToProject(@RequestParam(name = "employee") int empId,
                                                            @RequestParam(name = "project") int projectId) {
        Employee updated = managementService.addEmpByIdToProject(empId, projectId);
        EmployeeDTO dto = mapper.toDTO(updated);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping(value="company/manage/removeProject")
    public ResponseEntity<String> removeProject(@RequestParam(name = "employee") int empId,
                                                @RequestParam(name = "project") int projectId) {
        managementService.removeProjectFromEmployee(empId, projectId);
        return ResponseEntity.ok(
                String.format("Project with id = %d was deleted from user with id = %d", projectId, empId)
        );
    }
}
